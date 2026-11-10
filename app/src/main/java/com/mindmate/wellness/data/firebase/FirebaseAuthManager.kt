package com.mindmate.wellness.data.firebase

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.userProfileChangeRequest
import com.mindmate.wellness.data.model.UserProfile

class FirebaseAuthManager {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
    private val dbManager = FirebaseDatabaseManager()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isLoggedIn: Boolean
        get() = auth.currentUser != null

    fun login(
        email: String,
        pass: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    onSuccess(user)
                } else {
                    onError("Failed to retrieve user session.")
                }
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Login failed. Please check your credentials.")
            }
    }

    fun signUp(
        name: String,
        university: String,
        email: String,
        pass: String,
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.createUserWithEmailAndPassword(email, pass)
            .addOnSuccessListener { result ->
                val user = result.user
                if (user != null) {
                    // Update user display name in Firebase Auth
                    val profileUpdates = userProfileChangeRequest {
                        displayName = name
                    }
                    user.updateProfile(profileUpdates)

                    // Save user profile node to Realtime Database
                    val userProfile = UserProfile(
                        uid = user.uid,
                        displayName = name,
                        email = email,
                        university = university,
                        createdAt = System.currentTimeMillis()
                    )
                    dbManager.saveUserProfile(userProfile) { _ ->
                        onSuccess(user)
                    }
                } else {
                    onError("User creation returned empty profile.")
                }
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Sign up failed. Please try again.")
            }
    }

    fun resetPassword(
        email: String,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
        auth.sendPasswordResetEmail(email)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { e -> onError(e.localizedMessage ?: "Failed to send reset link.") }
    }

    fun signInAnonymously(
        onSuccess: (FirebaseUser) -> Unit,
        onError: (String) -> Unit
    ) {
        auth.signInAnonymously()
            .addOnSuccessListener { result ->
                result.user?.let(onSuccess) ?: onError("Anonymous sign in returned empty session.")
            }
            .addOnFailureListener { e ->
                onError(e.localizedMessage ?: "Anonymous session failed.")
            }
    }

    fun signOut() {
        auth.signOut()
    }
}
