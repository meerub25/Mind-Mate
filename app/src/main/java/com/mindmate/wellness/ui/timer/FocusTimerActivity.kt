package com.mindmate.wellness.ui.timer

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mindmate.wellness.R
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.data.model.MoodEntry
import com.mindmate.wellness.databinding.ActivityFocusTimerBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FocusTimerActivity : AppCompatActivity() {

    private lateinit var binding: ActivityFocusTimerBinding
    private lateinit var dbHelper: MindmateDatabaseHelper

    private var countDownTimer: CountDownTimer? = null
    private var totalTimeMs: Long = 25 * 60 * 1000L
    private var timeLeftMs: Long = 25 * 60 * 1000L
    private var isTimerRunning = false
    private var isBreakMode = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFocusTimerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        dbHelper = MindmateDatabaseHelper(this)

        updateDisplay()
        binding.progressFocusCircle.max = 1000
        binding.progressFocusCircle.progress = 1000

        binding.btnBackFocus.setOnClickListener { finish() }

        binding.btnToggleTimer.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.btnResetTimer.setOnClickListener {
            resetTimer()
        }

        binding.btnSet25Min.setOnClickListener {
            switchToMode(isBreak = false)
        }

        binding.btnSet5MinBreak.setOnClickListener {
            switchToMode(isBreak = true)
        }
    }

    private fun switchToMode(isBreak: Boolean) {
        pauseTimer()
        isBreakMode = isBreak
        totalTimeMs = if (isBreak) 5 * 60 * 1000L else 25 * 60 * 1000L
        timeLeftMs = totalTimeMs

        if (isBreak) {
            binding.tvFocusModeBadge.text = "Rest 5m"
            binding.tvFocusSessionLabel.text = "Mindful Rest & Rehydration"
            binding.btnSet5MinBreak.setBackgroundColor(getColor(R.color.mindease_purple))
            binding.btnSet5MinBreak.setTextColor(getColor(R.color.white))
            binding.btnSet25Min.setBackgroundColor(getColor(R.color.card_white))
            binding.btnSet25Min.setTextColor(getColor(R.color.text_secondary))
        } else {
            binding.tvFocusModeBadge.text = "Study 25m"
            binding.tvFocusSessionLabel.text = "Deep Work Block"
            binding.btnSet25Min.setBackgroundColor(getColor(R.color.mindease_purple))
            binding.btnSet25Min.setTextColor(getColor(R.color.white))
            binding.btnSet5MinBreak.setBackgroundColor(getColor(R.color.card_white))
            binding.btnSet5MinBreak.setTextColor(getColor(R.color.text_secondary))
        }

        updateDisplay()
        binding.progressFocusCircle.progress = 1000
    }

    private fun startTimer() {
        isTimerRunning = true
        binding.btnToggleTimer.text = "Pause"

        countDownTimer = object : CountDownTimer(timeLeftMs, 50) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftMs = millisUntilFinished
                updateDisplay()
                val progressFraction = (timeLeftMs.toFloat() / totalTimeMs) * 1000
                binding.progressFocusCircle.progress = progressFraction.toInt()
            }

            override fun onFinish() {
                isTimerRunning = false
                timeLeftMs = 0
                updateDisplay()
                binding.progressFocusCircle.progress = 0
                binding.btnToggleTimer.text = "Start Again"
                triggerHapticPulse()

                if (!isBreakMode) {
                    showPostSessionReflectionDialog()
                } else {
                    Toast.makeText(this@FocusTimerActivity, "Break complete! Ready to study? 🚀", Toast.LENGTH_LONG).show()
                }
            }
        }.start()
    }

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        binding.btnToggleTimer.text = "Resume"
    }

    private fun resetTimer() {
        pauseTimer()
        timeLeftMs = totalTimeMs
        updateDisplay()
        binding.progressFocusCircle.progress = 1000
        binding.btnToggleTimer.text = if (isBreakMode) "Start Break" else "Start Focus"
    }

    private fun updateDisplay() {
        val minutes = (timeLeftMs / 1000) / 60
        val seconds = (timeLeftMs / 1000) % 60
        binding.tvFocusTimeDisplay.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
    }

    private fun showPostSessionReflectionDialog() {
        val emotions = arrayOf("Accomplished ✨", "Relieved 😌", "Calm & Centered 🌿", "A bit tired 🥱")
        val moodScores = arrayOf(5, 4, 4, 3)

        MaterialAlertDialogBuilder(this)
            .setTitle("🎯 Focus Sprint Completed!")
            .setMessage("25 minutes of dedicated learning completed. How are you feeling right now?")
            .setItems(emotions) { _, which ->
                val chosenEmotion = emotions[which]
                val score = moodScores[which]
                val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                val entry = MoodEntry(
                    moodScore = score,
                    moodEmoji = when (which) {
                        0 -> "🎯"
                        1 -> "😌"
                        2 -> "🌿"
                        else -> "🥱"
                    },
                    moodLabel = chosenEmotion,
                    stressLevel = (11 - score * 2).coerceIn(1, 10),
                    triggers = listOf("Study Routine", "Exams"),
                    journalNote = "Finished 25m Focus Block",
                    dateString = todayStr
                )
                dbHelper.insertMood(entry)
                Toast.makeText(this, "Reflection recorded in your journal ✨", Toast.LENGTH_SHORT).show()
            }
            .setPositiveButton("Take 5m Break") { _, _ ->
                switchToMode(isBreak = true)
                startTimer()
            }
            .setNegativeButton("Done", null)
            .show()
    }

    private fun triggerHapticPulse() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 300, 200, 400), -1))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(500)
            }
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        countDownTimer?.cancel()
    }
}
