package com.mindmate.wellness.ui.stress

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.mindmate.wellness.databinding.ActivityGroundingBinding

class GroundingExerciseActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGroundingBinding
    private var currentStep = 0

    private val steps = listOf(
        GroundingStep(
            counter = "Step 1 of 5",
            emoji = "👀",
            title = "Look for 5 Things You Can SEE",
            desc = "Scan your room, hostel, or library right now. Name 5 specific objects around you (e.g. your textbook, pen, lamp, fan, window)."
        ),
        GroundingStep(
            counter = "Step 2 of 5",
            emoji = "✋",
            title = "Find 4 Things You Can TOUCH",
            desc = "Notice the physical texture of 4 items around you: feel your desk, your shirt fabric, the cool screen of your phone, or your feet grounded firmly on the floor."
        ),
        GroundingStep(
            counter = "Step 3 of 5",
            emoji = "👂",
            title = "Listen for 3 Things You Can HEAR",
            desc = "Close your eyes for a moment. Listen carefully for 3 subtle sounds: birds chirping, the hum of the fan/AC, footsteps outside, or your own breath."
        ),
        GroundingStep(
            counter = "Step 4 of 5",
            emoji = "👃",
            title = "Notice 2 Things You Can SMELL",
            desc = "Inhale gently. Try to detect 2 scents: the smell of your notebook paper, your tea/chai, fresh air, or your clothing."
        ),
        GroundingStep(
            counter = "Step 5 of 5",
            emoji = "👅",
            title = "Focus on 1 Thing You Can TASTE",
            desc = "Notice the taste inside your mouth: a sip of water, leftover tea, mint, or simply take a conscious swallow and acknowledge your presence right here."
        )
    )

    data class GroundingStep(
        val counter: String,
        val emoji: String,
        val title: String,
        val desc: String
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGroundingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackGrounding.setOnClickListener {
            finish()
        }

        displayStep(currentStep)

        binding.btnNextGroundingStep.setOnClickListener {
            if (currentStep < steps.size - 1) {
                currentStep++
                displayStep(currentStep)
            } else {
                // Completed
                binding.tvStepCounter.text = "Completed ✨"
                binding.tvStepEmoji.text = "🌱"
                binding.tvStepInstructionTitle.text = "You Are Grounded & Present"
                binding.tvStepInstructionDesc.text = "You successfully disconnected from racing anxiety and reconnected with the present moment. Take a deep, gentle breath and carry this calmness with you."
                binding.btnNextGroundingStep.text = "Finish Session"
                binding.btnNextGroundingStep.setOnClickListener { finish() }
            }
        }
    }

    private fun displayStep(index: Int) {
        val s = steps[index]
        binding.tvStepCounter.text = s.counter
        binding.tvStepEmoji.text = s.emoji
        binding.tvStepInstructionTitle.text = s.title
        binding.tvStepInstructionDesc.text = s.desc
        binding.btnNextGroundingStep.text = if (index == steps.size - 1) "Complete Grounding" else "I Found Them (Next)"
    }
}
