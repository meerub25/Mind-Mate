package com.mindmate.wellness.data.model

/**
 * Represents a student's profile synchronized with Firebase Realtime Database.
 */
data class UserProfile(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val university: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    // No-arg constructor required for Firebase Realtime Database deserialization
    constructor() : this("", "", "", "", 0L)
}
