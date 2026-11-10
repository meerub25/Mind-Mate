package com.mindmate.wellness.ui.insights

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.chip.Chip
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.databinding.FragmentInsightsBinding
import com.mindmate.wellness.ui.mood.LogMoodBottomSheetDialog

class InsightsFragment : Fragment() {

    private var _binding: FragmentInsightsBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MindmateDatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentInsightsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MindmateDatabaseHelper(requireContext())

        binding.btnEmptyLogCheckin.setOnClickListener {
            val dialog = LogMoodBottomSheetDialog {
                loadInsights()
            }
            dialog.show(parentFragmentManager, "LogMoodDialog")
        }

        loadInsights()
    }

    private fun loadInsights() {
        val allMoods = dbHelper.getAllMoodEntries()
        if (allMoods.isEmpty()) {
            binding.layoutEmptyInsights.visibility = View.VISIBLE
            binding.layoutInsightsContent.visibility = View.GONE
            return
        }

        binding.layoutEmptyInsights.visibility = View.GONE
        binding.layoutInsightsContent.visibility = View.VISIBLE

        // 1. 7-Day Trend Chart
        val trend = dbHelper.get7DayMoodTrend()
        if (trend.isNotEmpty()) {
            binding.insightsTrendChart.setTrendData(trend)
        }

        // 2. Average Stress
        val avgStress = dbHelper.getAverageStressLevel()
        val score = if (avgStress > 0) avgStress else 4.5f
        binding.tvWeeklyAvgStress.text = "Avg: %.1f / 10".format(score)
        binding.progressStressAvg.progress = (score * 10).toInt().coerceIn(0, 100)

        // 3. Triggers
        val topTriggers = dbHelper.getTopTriggers()
        binding.chipGroupTriggersInsights.removeAllViews()
        for (t in topTriggers) {
            val chip = Chip(requireContext()).apply {
                text = "${t.first} (${t.second})"
                isClickable = false
                isCheckable = false
            }
            binding.chipGroupTriggersInsights.addView(chip)
        }
    }

    override fun onResume() {
        super.onResume()
        loadInsights()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
