package com.mindmate.wellness.ui.mood

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.mindmate.wellness.R
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.data.model.MoodEntry
import com.mindmate.wellness.databinding.DialogLogMoodBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class LogMoodBottomSheetDialog(
    private val onSaved: () -> Unit
) : BottomSheetDialogFragment() {

    private var _binding: DialogLogMoodBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MindmateDatabaseHelper

    private var selectedMoodScore = 4
    private var selectedMoodEmoji = "🙂"
    private var selectedMoodLabel = "Okay"
    private var currentStressLevel = 5

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogLogMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MindmateDatabaseHelper(requireContext())

        setupMoodButtons()
        setupStressSlider()
        setupSaveButton()

        // Default selection: Okay
        selectMood(binding.btnMoodOkay, 4, "🙂", "Okay")
    }

    private fun setupMoodButtons() {
        binding.btnMoodGreat.setOnClickListener {
            selectMood(binding.btnMoodGreat, 5, "✨", "Great")
        }
        binding.btnMoodOkay.setOnClickListener {
            selectMood(binding.btnMoodOkay, 4, "🙂", "Okay")
        }
        binding.btnMoodMeh.setOnClickListener {
            selectMood(binding.btnMoodMeh, 3, "😐", "Meh")
        }
        binding.btnMoodLow.setOnClickListener {
            selectMood(binding.btnMoodLow, 2, "😔", "Low")
        }
        binding.btnMoodAwful.setOnClickListener {
            selectMood(binding.btnMoodAwful, 1, "😫", "Awful")
        }
    }

    private fun selectMood(button: TextView, score: Int, emoji: String, label: String) {
        selectedMoodScore = score
        selectedMoodEmoji = emoji
        selectedMoodLabel = label

        // Update Big Emoji and Label in Top Purple Block
        binding.tvSelectedMoodEmojiBig.text = emoji
        binding.tvSelectedMoodNameBig.text = label

        val buttons = listOf(
            binding.btnMoodGreat,
            binding.btnMoodOkay,
            binding.btnMoodMeh,
            binding.btnMoodLow,
            binding.btnMoodAwful
        )

        for (btn in buttons) {
            if (btn == button) {
                btn.setBackgroundResource(R.drawable.bg_card_wellness)
                btn.setTextColor(Color.parseColor("#5B4FE0"))
            } else {
                btn.setBackgroundColor(Color.TRANSPARENT)
                btn.setTextColor(Color.parseColor("#6B7280"))
            }
        }
    }

    private fun setupStressSlider() {
        binding.sliderStress.addOnChangeListener { _, value, _ ->
            currentStressLevel = value.toInt()
            binding.tvStressValue.text = "$currentStressLevel/10"
        }
    }

    private fun setupSaveButton() {
        binding.btnSaveMoodEntry.setOnClickListener {
            val selectedTriggers = mutableListOf<String>()
            for (i in 0 until binding.chipGroupTriggers.childCount) {
                val chip = binding.chipGroupTriggers.getChildAt(i) as? Chip
                if (chip?.isChecked == true) {
                    selectedTriggers.add(chip.text.toString())
                }
            }

            val note = binding.etJournalNote.text?.toString()?.trim() ?: ""
            val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val dateStr = dateFormat.format(Date())

            val entry = MoodEntry(
                moodScore = selectedMoodScore,
                moodEmoji = selectedMoodEmoji,
                moodLabel = selectedMoodLabel,
                stressLevel = currentStressLevel,
                triggers = selectedTriggers,
                journalNote = note,
                dateString = dateStr
            )

            dbHelper.insertMood(entry)

            // Sync with Firebase Realtime Database if authenticated
            val user = com.google.firebase.auth.FirebaseAuth.getInstance().currentUser
            if (user != null && !user.isAnonymous) {
                com.mindmate.wellness.data.firebase.FirebaseDatabaseManager().syncMoodEntry(user.uid, entry)
            }

            onSaved()
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
