package com.mindmate.wellness.ui.panic

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.appcompat.app.AppCompatActivity
import com.mindmate.wellness.databinding.ActivityPanicModeBinding

class PanicModeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPanicModeBinding
    private val handler = Handler(Looper.getMainLooper())

    private var phaseIndex = 0
    private var secondCounter = 0
    private var cycleCounter = 0

    private val phases = listOf(
        Pair("Inhale slowly...", 1.0f),
        Pair("Hold gently...", 1.0f),
        Pair("Exhale completely...", 0.3f),
        Pair("Rest...", 0.3f)
    )

    private val groundingSteps = listOf(
        "🦶 Feel your feet firmly touching the floor. You are grounded. You are safe.",
        "🖐️ Rest both hands flat on your chest or lap. Feel your steady warmth.",
        "👀 Notice 3 blue or green objects around you. Your body is calming down.",
        "✨ Panic always passes. You have survived every difficult moment before this."
    )

    private val panicRunnable = object : Runnable {
        override fun run() {
            secondCounter++
            val fraction = secondCounter / 4.0f
            val target = phases[phaseIndex].second
            val prev = if (phaseIndex == 0) 0.3f else phases[phaseIndex - 1].second
            val progress = prev + (target - prev) * fraction

            binding.panicPacerView.setProgress(progress)

            if (secondCounter >= 4) {
                secondCounter = 0
                phaseIndex = (phaseIndex + 1) % phases.size
                binding.tvPanicPhase.text = phases[phaseIndex].first
                triggerSoftPulse()

                if (phaseIndex == 0) {
                    cycleCounter = (cycleCounter + 1) % groundingSteps.size
                    binding.tvPanicGroundingTip.text = groundingSteps[cycleCounter]
                }
            }

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPanicModeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnExitPanicMode.setOnClickListener {
            finish()
        }

        binding.btnPanicHelpline.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:03117786264"))
            startActivity(intent)
        }

        triggerSoftPulse()
        handler.post(panicRunnable)
    }

    private fun triggerSoftPulse() {
        try {
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator?.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator?.vibrate(40)
            }
        } catch (_: Exception) {}
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(panicRunnable)
    }
}
