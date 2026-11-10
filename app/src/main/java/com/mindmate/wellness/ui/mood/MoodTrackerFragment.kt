package com.mindmate.wellness.ui.mood

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.databinding.FragmentMoodBinding

class MoodTrackerFragment : Fragment() {

    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MindmateDatabaseHelper
    private lateinit var adapter: MoodHistoryAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MindmateDatabaseHelper(requireContext())

        adapter = MoodHistoryAdapter()
        binding.rvMoodHistory.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMoodHistory.adapter = adapter

        binding.btnLogMoodHeader.setOnClickListener {
            showLogMoodDialog()
        }

        loadHistory()
    }

    private fun showLogMoodDialog() {
        val dialog = LogMoodBottomSheetDialog {
            loadHistory()
        }
        dialog.show(parentFragmentManager, "LogMoodDialog")
    }

    private fun loadHistory() {
        val items = dbHelper.getAllMoodEntries()
        adapter.submitList(items)
        if (items.isEmpty()) {
            binding.tvEmptyMoods.visibility = View.VISIBLE
            binding.rvMoodHistory.visibility = View.GONE
        } else {
            binding.tvEmptyMoods.visibility = View.GONE
            binding.rvMoodHistory.visibility = View.VISIBLE
        }
    }

    override fun onResume() {
        super.onResume()
        loadHistory()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
