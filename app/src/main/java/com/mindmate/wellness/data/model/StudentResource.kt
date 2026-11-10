package com.mindmate.wellness.data.model

/**
 * Verified mental health services, student counseling centers, and crisis hotlines in Pakistan.
 */
data class StudentResource(
    val id: String,
    val name: String,
    val description: String,
    val contactNumber: String,
    val operatingHours: String,
    val is24Hours: Boolean,
    val category: String, // "Crisis Helpline", "Student Counseling", "Substance / Burnout Support"
    val isFree: Boolean = true
)
