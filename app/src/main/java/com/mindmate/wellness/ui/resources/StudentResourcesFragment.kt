package com.mindmate.wellness.ui.resources

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.mindmate.wellness.R
import com.mindmate.wellness.data.model.StudentResource
import com.mindmate.wellness.databinding.FragmentResourcesBinding

class StudentResourcesFragment : Fragment() {

    private var _binding: FragmentResourcesBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ResourcesAdapter

    private val allResources = listOf(
        StudentResource(
            id = "umang",
            name = "Umang Pakistan 24/7 Helpline",
            description = "Pakistan's first 24/7 free mental health helpline accredited by WHO. Safe, confidential space for university students battling exam anxiety, loneliness, and depression.",
            contactNumber = "0311-7786264",
            operatingHours = "24/7",
            is24Hours = true,
            category = "Helpline"
        ),
        StudentResource(
            id = "rozan",
            name = "Rozan Counseling Helpline",
            description = "Toll-free, confidential emotional support and counseling for youth, addressing family pressure, academic strain, and emotional well-being.",
            contactNumber = "0800-22444",
            operatingHours = "Mon - Fri: 10 AM to 6 PM",
            is24Hours = false,
            category = "Helpline"
        ),
        StudentResource(
            id = "taskeen",
            name = "Taskeen Health Initiative",
            description = "Provides free telephonic emotional counseling, stress management guidance, and psychological support for students across Pakistan.",
            contactNumber = "0316-8275336",
            operatingHours = "Mon - Sat: 9 AM to 11 PM",
            is24Hours = false,
            category = "Helpline"
        ),
        StudentResource(
            id = "talk2me",
            name = "Talk2Me Pakistan",
            description = "Student-centric counseling focusing on career anxiety, campus life adjustment, relationship challenges, and self-esteem.",
            contactNumber = "0333-5577666",
            operatingHours = "Mon - Sun: 12 PM to 10 PM",
            is24Hours = false,
            category = "Helpline"
        ),
        StudentResource(
            id = "lums",
            name = "LUMS Student Counseling Center",
            description = "On-campus individual counseling, exam anxiety workshops, and peer support network for enrolled students.",
            contactNumber = "042-35608000",
            operatingHours = "Mon - Fri: 9 AM to 5 PM",
            is24Hours = false,
            category = "On-Campus"
        ),
        StudentResource(
            id = "nust",
            name = "NUST Center for Counseling (C3A)",
            description = "Confidential psychological counseling, career aptitude guidance, and stress relief programs for NUST students in Islamabad.",
            contactNumber = "051-90851571",
            operatingHours = "Mon - Fri: 9 AM to 5 PM",
            is24Hours = false,
            category = "On-Campus"
        ),
        StudentResource(
            id = "iba",
            name = "IBA Karachi Wellness & Guidance",
            description = "Professional counselors dedicated to supporting undergraduate and graduate students with stress, deadlines, and hostel adjustments.",
            contactNumber = "021-38104700",
            operatingHours = "Mon - Fri: 9 AM to 6 PM",
            is24Hours = false,
            category = "On-Campus"
        ),
        StudentResource(
            id = "fast",
            name = "FAST-NUCES Student Advisory Service",
            description = "Faculty student advisors and psychological support network across Lahore, Islamabad, Karachi, Peshawar, and CFD campuses.",
            contactNumber = "042-111128128",
            operatingHours = "Mon - Fri: 9 AM to 4 PM",
            is24Hours = false,
            category = "On-Campus"
        ),
        StudentResource(
            id = "synapse",
            name = "Synapse Pakistan Neuroscience Clinic",
            description = "Vetted psychologists and psychiatrists providing specialized cognitive behavioral therapy, anxiety therapy, and virtual appointments.",
            contactNumber = "021-35841000",
            operatingHours = "Mon - Sat: 10 AM to 7 PM",
            is24Hours = false,
            category = "Professional"
        ),
        StudentResource(
            id = "aas",
            name = "AAS Trust Counseling Clinic",
            description = "Comprehensive psychological support, burnout rehabilitation, and emotional counseling for university youth and young adults.",
            contactNumber = "021-35868205",
            operatingHours = "Mon - Sat: 9 AM to 5 PM",
            is24Hours = false,
            category = "Professional"
        ),
        StudentResource(
            id = "sehat",
            name = "National Emergency Helpline (1122)",
            description = "Immediate emergency response for medical and psychiatric crises across Punjab, KP, Sindh, and Islamabad.",
            contactNumber = "1122",
            operatingHours = "24/7",
            is24Hours = true,
            category = "Helpline"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentResourcesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ResourcesAdapter(allResources)
        binding.rvResources.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResources.adapter = adapter

        setupCategoryChips()
    }

    private fun setupCategoryChips() {
        binding.chipGroupResourceCategory.setOnCheckedStateChangeListener { _, checkedIds ->
            val filtered = when {
                checkedIds.contains(R.id.chipResHelplines) -> allResources.filter { it.category == "Helpline" }
                checkedIds.contains(R.id.chipResCampus) -> allResources.filter { it.category == "On-Campus" }
                checkedIds.contains(R.id.chipResProfessionals) -> allResources.filter { it.category == "Professional" }
                else -> allResources
            }
            binding.rvResources.adapter = ResourcesAdapter(filtered)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
