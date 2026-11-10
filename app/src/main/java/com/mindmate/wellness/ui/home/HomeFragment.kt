package com.mindmate.wellness.ui.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.mindmate.wellness.R
import com.mindmate.wellness.api.quotes.QuoteRepository
import com.mindmate.wellness.api.weather.WeatherRepository
import com.mindmate.wellness.data.firebase.FirebaseAuthManager
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.databinding.FragmentHomeBinding
import com.mindmate.wellness.ui.MainActivity
import com.mindmate.wellness.ui.mood.LogMoodBottomSheetDialog
import com.mindmate.wellness.ui.stress.BreathingExerciseActivity
import com.mindmate.wellness.ui.support.SosBottomSheetDialog
import kotlinx.coroutines.launch
import java.util.Calendar

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MindmateDatabaseHelper
    private val authManager = FirebaseAuthManager()
    private val quoteRepository = QuoteRepository()
    private val weatherRepository = WeatherRepository()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MindmateDatabaseHelper(requireContext())

        setupTopHeader()
        setupListeners()
        updateRecentCheckin()
        fetchLiveApis()
    }

    private fun setupTopHeader() {
        val user = authManager.currentUser
        val name = when {
            user != null && !user.displayName.isNullOrBlank() -> user.displayName!!
            else -> {
                val prefs = requireContext().getSharedPreferences("mindease_prefs", Context.MODE_PRIVATE)
                prefs.getString("user_nickname", "Student") ?: "Student"
            }
        }

        binding.tvUserName.text = name
        binding.tvUserAvatar.text = name.firstOrNull()?.uppercase() ?: "S"

        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
        val timeGreeting = when (hour) {
            in 4..11 -> "Good morning,"
            in 12..16 -> "Good afternoon,"
            else -> "Good evening,"
        }
        binding.tvGreetingTime.text = timeGreeting
    }

    private fun updateRecentCheckin() {
        val today = dbHelper.getTodayMoodEntry()
        if (today != null) {
            binding.tvRecentCheckinStatus.text = "${today.moodEmoji} Feeling ${today.moodLabel} • Stress: ${today.stressLevel}/10"
        } else {
            val all = dbHelper.getAllMoodEntries()
            if (all.isNotEmpty()) {
                val latest = all[0]
                binding.tvRecentCheckinStatus.text = "${latest.moodEmoji} Last check-in: ${latest.moodLabel} (${latest.dateString})"
            } else {
                binding.tvRecentCheckinStatus.text = "No check-ins yet today. Tap above to log how you feel!"
            }
        }
    }

    private fun fetchLiveApis() {
        viewLifecycleOwner.lifecycleScope.launch {
            val quote = quoteRepository.getDailyQuote()
            binding.tvDailyQuoteHome.text = "\"${quote.quote}\" — ${quote.author}"
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val weather = weatherRepository.getWeatherForCity("Lahore", 31.5204, 74.3587)
            binding.tvCampusWeatherInline.text = "${weather.weatherEmoji} %.0f°C".format(weather.temperature)
        }
    }

    private fun setupListeners() {
        // Hero Check in now
        binding.btnHeroCheckin.setOnClickListener {
            val dialog = LogMoodBottomSheetDialog {
                updateRecentCheckin()
            }
            dialog.show(parentFragmentManager, "LogMoodDialog")
        }

        // Talk it out -> Chat tab
        binding.cardTalkItOut.setOnClickListener {
            (activity as? MainActivity)?.selectNavigationTab(R.id.nav_chat)
        }

        // Feel calmer -> Breathing / Self Care
        binding.cardFeelCalmer.setOnClickListener {
            startActivity(Intent(requireContext(), BreathingExerciseActivity::class.java))
        }

        // Settings / More tab
        binding.btnHomeSettings.setOnClickListener {
            (activity as? MainActivity)?.selectNavigationTab(R.id.nav_more)
        }

        // Topic chips (open Chat with pre-filled topic)
        binding.chipTopicExams.setOnClickListener {
            openChatWithTopic("I am feeling overwhelmed with exam and CGPA stress.")
        }
        binding.chipTopicCareer.setOnClickListener {
            openChatWithTopic("I am worried about career uncertainty and job market competition.")
        }
        binding.chipTopicFamily.setOnClickListener {
            openChatWithTopic("I feel pressure from family expectations and comparison.")
        }
        binding.chipTopicMoney.setOnClickListener {
            openChatWithTopic("I have financial concerns regarding semester expenses and tuition.")
        }
        binding.chipTopicLonely.setOnClickListener {
            openChatWithTopic("I feel lonely and isolated in my hostel.")
        }

        // Floating SOS Button -> Open SOS emergency panel
        binding.fabSos.setOnClickListener {
            val dialog = SosBottomSheetDialog()
            dialog.show(parentFragmentManager, "SosDialog")
        }
    }

    private fun openChatWithTopic(topicPrompt: String) {
        val prefs = requireContext().getSharedPreferences("mindease_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString("pending_chat_prompt", topicPrompt).apply()
        (activity as? MainActivity)?.selectNavigationTab(R.id.nav_chat)
    }

    override fun onResume() {
        super.onResume()
        setupTopHeader()
        updateRecentCheckin()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
