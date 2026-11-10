package com.mindmate.wellness.data.model

enum class MessageSender {
    USER,
    AI
}

/**
 * Represents an interaction between the student and the AI Emotional Companion.
 */
data class ChatMessage(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val sender: MessageSender,
    val content: String,
    val language: String = "en", // "en", "ur", "roman_ur"
    val detectedEmotion: String? = null,
    val suggestedAction: String? = null
)
