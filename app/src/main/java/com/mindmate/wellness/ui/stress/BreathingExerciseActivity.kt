package com.mindmate.wellness.ui.stress

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import com.mindmate.wellness.R
import com.mindmate.wellness.databinding.ActivityBreathingBinding

class BreathingExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBreathingBinding
    private val handler = Handler(Looper.getMainLooper())

    private var isRunning = false
    private var currentPhaseIndex = 0 // 0: Inhale, 1: Hold, 2: Exhale, 3: Rest
    private var secondsInPhase = 0
    private val phaseDuration = 4 // 4-4-4-4 Box Breathing

    private val phases = listOf(
        Pair("Inhale slowly through nose...", 1.0f),
        Pair("Hold gently...", 1.0f),
        Pair("Exhale completely through mouth...", 0.3f),
        Pair("Rest comfortably...", 0.3f)
    )

    private val breathingRunnable = object : Runnable {
        override fun run() {
            if (!isRunning) return

            secondsInPhase++
            val remainingSeconds = phaseDuration - secondsInPhase

            // Update progress of pacer view
            val currentPhaseTarget = phases[currentPhaseIndex].second
            val prevPhaseTarget = if (currentPhaseIndex == 0) 0.3f else phases[currentPhaseIndex - 1].second
            val fraction = secondsInPhase.toFloat() / phaseDuration
            val currentProgress = prevPhaseTarget + (currentPhaseTarget - prevPhaseTarget) * fraction

            binding.breathingPacerView.setProgress(currentProgress)
            binding.tvBreathingSeconds.text = "${remainingSeconds + 1}s"

            if (secondsInPhase >= phaseDuration) {
                // Transition to next phase
                secondsInPhase = 0
                currentPhaseIndex = (currentPhaseIndex + 1) % phases.size
                triggerHapticPulse()
                binding.tvBreathingPhase.text = phases[currentPhaseIndex].first
            }

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBreathingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackBreathing.setOnClickListener {
            finish()
        }

        binding.btnToggleBreathing.setOnClickListener {
            if (isRunning) {
                stopBreathing()
            } else {
                startBreathing()
            }
        }
    }

    private fun startBreathing() {
        isRunning = true
        currentPhaseIndex = 0
        secondsInPhase = 0
        binding.tvBreathingPhase.text = phases[0].first
        binding.btnToggleBreathing.text = getString(R.string.breathing_btn_stop)
        binding.btnToggleBreathing.setBackgroundColor(getColor(R.color.brand_rose))
        triggerHapticPulse()
        handler.post(breathingRunnable)
    }

    private fun stopBreathing() {
        isRunning = false
        handler.removeCallbacks(breathingRunnable)
        binding.btnToggleBreathing.text = getString(R.string.breathing_btn_start)
        binding.btnToggleBreathing.setBackgroundColor(getColor(R.color.brand_emerald))
        binding.tvBreathingPhase.text = "Session Complete. Great job!"
        binding.tvBreathingSeconds.text = ""
        binding.breathingPacerView.setProgress(0.5f)
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
                vibrator?.vibrate(VibrationEffect.createOneShot(35, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(35)
            }
        } catch (_: Exception) {
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(breathingRunnable)
    }
}
