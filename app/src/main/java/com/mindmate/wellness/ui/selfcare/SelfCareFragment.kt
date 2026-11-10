package com.mindmate.wellness.ui.selfcare

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mindmate.wellness.R
import com.mindmate.wellness.databinding.FragmentSelfCareBinding
import com.mindmate.wellness.ui.stress.GroundingExerciseActivity
import com.mindmate.wellness.ui.timer.FocusTimerActivity

class SelfCareFragment : Fragment() {

    private var _binding: FragmentSelfCareBinding? = null
    private val binding get() = _binding!!

    private val handler = Handler(Looper.getMainLooper())
    private var isBreathingActive = false
    private var phaseIndex = 0
    private var secondsInPhase = 0
    private val phaseDuration = 4 // 4-4-4-4 Box Breathing

    private val phases = listOf(
        Pair("Inhale...", 1.0f),
        Pair("Hold...", 1.0f),
        Pair("Exhale...", 0.3f),
        Pair("Hold...", 0.3f)
    )

    private val breathingRunnable = object : Runnable {
        override fun run() {
            if (!isBreathingActive || _binding == null) return

            secondsInPhase++
            val currentPhaseTarget = phases[phaseIndex].second
            val prevPhaseTarget = if (phaseIndex == 0) 0.3f else phases[phaseIndex - 1].second
            val fraction = secondsInPhase.toFloat() / phaseDuration
            val currentProgress = prevPhaseTarget + (currentPhaseTarget - prevPhaseTarget) * fraction

            binding.selfCareOrbPacer.setProgress(currentProgress)

            if (secondsInPhase >= phaseDuration) {
                secondsInPhase = 0
                phaseIndex = (phaseIndex + 1) % phases.size
                triggerHapticPulse()
                binding.tvOrbPhaseLabel.text = phases[phaseIndex].first
            }

            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSelfCareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnToggleSelfCareBreathing.setOnClickListener {
            if (isBreathingActive) {
                stopBreathing()
            } else {
                startBreathing()
            }
        }

        binding.cardActivityGrounding.setOnClickListener {
            startActivity(Intent(requireContext(), GroundingExerciseActivity::class.java))
        }

        binding.cardActivityPmr.setOnClickListener {
            showPmrDialog()
        }

        binding.cardActivityStudyRoutine.setOnClickListener {
            startActivity(Intent(requireContext(), FocusTimerActivity::class.java))
        }
    }

    private fun startBreathing() {
        isBreathingActive = true
        phaseIndex = 0
        secondsInPhase = 0
        binding.tvOrbPhaseLabel.text = phases[0].first
        binding.btnToggleSelfCareBreathing.text = getString(R.string.self_care_stop_breathing)
        triggerHapticPulse()
        handler.post(breathingRunnable)
    }

    private fun stopBreathing() {
        isBreathingActive = false
        handler.removeCallbacks(breathingRunnable)
        binding.btnToggleSelfCareBreathing.text = getString(R.string.self_care_start_breathing)
        binding.tvOrbPhaseLabel.text = "Inhale"
        binding.selfCareOrbPacer.setProgress(0.5f)
    }

    private fun showPmrDialog() {
        val steps = arrayOf(
            "1. Shoulders: Shrug towards ears tightly for 5s, exhale and drop completely.",
            "2. Hands: Clench fists tightly for 5s, then release and spread fingers wide.",
            "3. Jaw & Forehead: Scrunch face tightly for 5s, exhale and let face soften.",
            "4. Belly: Tighten stomach muscles for 5s, exhale deeply and let belly relax.",
            "5. Feet: Curl toes downward for 5s, release and feel your feet flat on the floor."
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("💆 Progressive Muscle Relaxation")
            .setItems(steps, null)
            .setPositiveButton("Done", null)
            .show()
    }

    private fun triggerHapticPulse() {
        try {
            val ctx = context ?: return
            val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = ctx.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator
            } else {
                @Suppress("DEPRECATION")
                ctx.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
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

    override fun onDestroyView() {
        super.onDestroyView()
        isBreathingActive = false
        handler.removeCallbacks(breathingRunnable)
        _binding = null
    }
}
