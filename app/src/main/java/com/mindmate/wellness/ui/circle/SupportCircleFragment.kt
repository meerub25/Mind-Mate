package com.mindmate.wellness.ui.circle

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.mindmate.wellness.R
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.data.local.SupportCirclePost
import com.mindmate.wellness.databinding.DialogShareCirclePostBinding
import com.mindmate.wellness.databinding.FragmentSupportCircleBinding
import com.mindmate.wellness.ui.support.SosBottomSheetDialog

class SupportCircleFragment : Fragment() {

    private var _binding: FragmentSupportCircleBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MindmateDatabaseHelper
    private lateinit var adapter: SupportCircleAdapter
    private var allPosts = listOf<SupportCirclePost>()
    private var currentFilter = "All"

    private val aliases = listOf(
        "Anonymous Owl",
        "Quiet Pine",
        "Blue Sparrow",
        "Gentle River",
        "Resilient Falcon",
        "Thoughtful Fox",
        "Calm Cloud",
        "Silent Star"
    )

    private val crisisKeywords = listOf(
        "suicide", "kill myself", "end my life", "want to die",
        "end it all", "self harm", "cutting myself", "khudkushi", "khud kushi",
        "marna chahta", "marna chahti", "jaan de dunga", "zindagi khatam"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportCircleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MindmateDatabaseHelper(requireContext())

        setupRecyclerView()
        setupFilters()
        loadPosts()

        binding.fabShareCirclePost.setOnClickListener {
            showCreatePostBottomSheet()
        }
    }

    private fun setupRecyclerView() {
        adapter = SupportCircleAdapter(mutableListOf()) { post ->
            dbHelper.incrementMeToo(post.id)
        }
        binding.rvCirclePosts.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCirclePosts.adapter = adapter
    }

    private fun setupFilters() {
        binding.chipGroupCircleFilters.setOnCheckedStateChangeListener { _, checkedIds ->
            currentFilter = when {
                checkedIds.contains(R.id.filterExams) -> "Exams"
                checkedIds.contains(R.id.filterHostel) -> "Hostel"
                checkedIds.contains(R.id.filterCareer) -> "Career"
                checkedIds.contains(R.id.filterLoneliness) -> "Lonely"
                else -> "All"
            }
            applyFilter()
        }
    }

    private fun loadPosts() {
        allPosts = dbHelper.getAllCirclePosts()
        applyFilter()
    }

    private fun applyFilter() {
        val filtered = if (currentFilter == "All") {
            allPosts
        } else {
            allPosts.filter { post ->
                post.content.contains(currentFilter, ignoreCase = true) ||
                (currentFilter == "Lonely" && post.content.contains("alone", ignoreCase = true))
            }
        }

        adapter.updateList(filtered)
        binding.layoutCircleEmpty.visibility = if (filtered.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun showCreatePostBottomSheet() {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = DialogShareCirclePostBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        val assignedAlias = aliases.random()
        sheetBinding.tvCirclePostingAs.text = "Posting as: $assignedAlias • 100% anonymous"

        sheetBinding.btnSubmitCirclePost.setOnClickListener {
            val content = sheetBinding.etCirclePostContent.text.toString().trim()
            if (content.isEmpty()) {
                sheetBinding.etCirclePostContent.error = "Please write a thought to share"
                return@setOnClickListener
            }

            // Keyword safety check for self-harm / crisis
            val lower = content.lowercase()
            val containsCrisis = crisisKeywords.any { lower.contains(it) }

            if (containsCrisis) {
                dialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "We care deeply about your safety. Connecting you with urgent human help...",
                    Toast.LENGTH_LONG
                ).show()
                SosBottomSheetDialog().show(parentFragmentManager, "SOS_FROM_CIRCLE")
                return@setOnClickListener
            }

            // Add topic tag prefix if selected
            val selectedTopic = when {
                sheetBinding.chipTopicExams.isChecked -> "[Exams] "
                sheetBinding.chipTopicHostel.isChecked -> "[Hostel] "
                sheetBinding.chipTopicCareer.isChecked -> "[Career] "
                sheetBinding.chipTopicLoneliness.isChecked -> "[Loneliness] "
                else -> ""
            }

            val finalContent = "$selectedTopic$content"
            dbHelper.insertCirclePost(assignedAlias, finalContent)
            dialog.dismiss()
            loadPosts()
            binding.rvCirclePosts.smoothScrollToPosition(0)
            Toast.makeText(requireContext(), "Shared anonymously with peers ✨", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
