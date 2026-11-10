package com.mindmate.wellness.data.firebase

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mindmate.wellness.data.model.MoodEntry
import com.mindmate.wellness.data.model.UserProfile

class FirebaseDatabaseManager {

    private val database: FirebaseDatabase by lazy {
        val instance = FirebaseDatabase.getInstance()
        try {
            // Enable offline caching for Realtime Database
            instance.setPersistenceEnabled(true)
        } catch (_: Exception) {
            // Persistence can only be set once
        }
        instance
    }

    private val usersRef = database.getReference("users")

    fun saveUserProfile(profile: UserProfile, onComplete: (Boolean) -> Unit) {
        usersRef.child(profile.uid).child("profile")
            .setValue(profile)
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun getUserProfile(uid: String, onResult: (UserProfile?) -> Unit) {
        usersRef.child(uid).child("profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val profile = snapshot.getValue(UserProfile::class.java)
                    onResult(profile)
                }

                override fun onCancelled(error: DatabaseError) {
                    onResult(null)
                }
            })
    }

    fun syncMoodEntry(uid: String, moodEntry: MoodEntry) {
        val moodsRef = usersRef.child(uid).child("moods")
        val newKey = moodsRef.push().key ?: return

        val moodMap = mapOf(
            "timestamp" to moodEntry.timestamp,
            "moodScore" to moodEntry.moodScore,
            "moodEmoji" to moodEntry.moodEmoji,
            "moodLabel" to moodEntry.moodLabel,
            "stressLevel" to moodEntry.stressLevel,
            "triggers" to moodEntry.triggers,
            "journalNote" to moodEntry.journalNote,
            "dateString" to moodEntry.dateString
        )
        moodsRef.child(newKey).setValue(moodMap)
    }
}
