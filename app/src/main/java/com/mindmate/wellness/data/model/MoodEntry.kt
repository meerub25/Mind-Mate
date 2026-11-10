package com.mindmate.wellness.data.model

/**
 * Represents a student's daily or spot emotional check-in.
 */
data class MoodEntry(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val moodScore: Int, // 1: Overwhelmed, 2: Down, 3: Okay, 4: Good, 5: Thriving
    val moodEmoji: String,
    val moodLabel: String,
    val stressLevel: Int, // 1 to 10
    val triggers: List<String>,
    val journalNote: String,
    val dateString: String
)
