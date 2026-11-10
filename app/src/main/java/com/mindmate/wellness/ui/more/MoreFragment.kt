package com.mindmate.wellness.ui.more

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mindmate.wellness.R
import com.mindmate.wellness.databinding.FragmentMoreBinding
import com.mindmate.wellness.ui.circle.SupportCircleFragment
import com.mindmate.wellness.ui.insights.InsightsFragment
import com.mindmate.wellness.ui.profile.ProfileFragment
import com.mindmate.wellness.ui.resources.StudentResourcesFragment
import com.mindmate.wellness.ui.selfcare.SelfCareFragment
import com.mindmate.wellness.ui.timer.FocusTimerActivity

class MoreFragment : Fragment() {

    private var _binding: FragmentMoreBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoreBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.cardMoreInsights.setOnClickListener {
            navigateTo(InsightsFragment())
        }

        binding.cardMoreSelfCare.setOnClickListener {
            navigateTo(SelfCareFragment())
        }

        binding.cardMoreCircle.setOnClickListener {
            navigateTo(SupportCircleFragment())
        }

        binding.cardMoreFinder.setOnClickListener {
            navigateTo(StudentResourcesFragment())
        }

        binding.cardMoreFocusTimer.setOnClickListener {
            startActivity(Intent(requireContext(), FocusTimerActivity::class.java))
        }

        binding.cardMoreProfile.setOnClickListener {
            navigateTo(ProfileFragment())
        }
    }

    private fun navigateTo(fragment: Fragment) {
        parentFragmentManager.beginTransaction()
            .setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out, android.R.anim.fade_in, android.R.anim.fade_out)
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
