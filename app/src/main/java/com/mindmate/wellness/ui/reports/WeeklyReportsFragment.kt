package com.mindmate.wellness.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.mindmate.wellness.R
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.databinding.FragmentReportsBinding

class WeeklyReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MindmateDatabaseHelper

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MindmateDatabaseHelper(requireContext())
        loadAnalyticsData()
    }

    private fun loadAnalyticsData() {
        // 1. Streak
        val streak = dbHelper.getCheckinStreak()
        binding.tvStreakCount.text = getString(R.string.reports_streak_days, streak)

        // 2. 7-Day Trend
        val trend = dbHelper.get7DayMoodTrend()
        if (trend.isNotEmpty()) {
            binding.moodTrendChart.setTrendData(trend)
        }

        // 3. Average Stress
        val avgStress = dbHelper.getAverageStressLevel()
        if (avgStress > 0f) {
            binding.tvAvgStressScore.text = "%.1f / 10".format(avgStress)
            binding.tvStressStatus.text = when {
                avgStress <= 3.5f -> "Low / Calm State"
                avgStress <= 6.5f -> "Moderate Academic Strain"
                else -> "High Stress / Near Burnout"
            }
        } else {
            binding.tvAvgStressScore.text = "4.5 / 10"
            binding.tvStressStatus.text = "Moderate Strain"
        }

        // 4. Top Triggers
        val topTriggers = dbHelper.getTopTriggers()
        if (topTriggers.isNotEmpty()) {
            val primary = topTriggers[0]
            binding.tvTopTriggerName.text = primary.first
            binding.tvTopTriggerDetail.text = "Triggered ${primary.second} time(s) recently"
        } else {
            binding.tvTopTriggerName.text = "Exams & Quizzes"
            binding.tvTopTriggerDetail.text = "Most common student stressor"
        }

        // 5. AI Pattern Identification Insight
        generateInsight(avgStress, topTriggers)
    }

    private fun generateInsight(avgStress: Float, topTriggers: List<Pair<String, Int>>) {
        val triggerNames = topTriggers.map { it.first }
        val insight = when {
            avgStress >= 7.0f -> {
                "⚠️ High Stress Pattern Detected: You've logged acute stress over recent days. When exams and deadlines pile up, try scheduling intentional 15-minute breaks. Remember: your worth is not defined by grades."
            }
            triggerNames.contains("Hostel Life") || triggerNames.contains("Friendships & Social") -> {
                "💡 Social & Environment Insight: You frequently report feeling drained in hostel settings. Connecting with family or a trusted batchmate can significantly reduce feelings of isolation."
            }
            triggerNames.contains("Exams & Quizzes") || triggerNames.contains("CGPA & Grades") -> {
                "📚 Academic Insight: Exam pressure is your primary emotional trigger. Practicing 2 minutes of box breathing before sitting down for study sessions helps calm test anxiety."
            }
            else -> {
                "✨ Balanced Momentum: Keep logging your check-ins consistently. Developing regular emotional awareness helps you spot academic burnout early before exam season begins."
            }
        }
        binding.tvPatternInsightText.text = insight
    }

    override fun onResume() {
        super.onResume()
        loadAnalyticsData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
