package com.mindmate.wellness.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.animation.DecelerateInterpolator
import androidx.appcompat.app.AppCompatActivity
import com.mindmate.wellness.data.firebase.FirebaseAuthManager
import com.mindmate.wellness.databinding.ActivitySplashBinding
import com.mindmate.wellness.ui.auth.LoginActivity
import com.mindmate.wellness.ui.lock.AppLockActivity
import com.mindmate.wellness.ui.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding
    private val authManager = FirebaseAuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Soft fade & scale animation
        binding.splashCenterContent.alpha = 0f
        binding.splashCenterContent.scaleX = 0.85f
        binding.splashCenterContent.scaleY = 0.85f

        binding.splashCenterContent.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(1200)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                proceedToNextScreen()
            }
            .start()
    }

    private fun proceedToNextScreen() {
        val prefs = getSharedPreferences("mindease_prefs", Context.MODE_PRIVATE)
        val hasCompletedOnboarding = prefs.getBoolean("has_completed_onboarding", false)
        val hasPinLock = prefs.getString("app_pin", null) != null

        val destination = when {
            !hasCompletedOnboarding -> OnboardingActivity::class.java
            hasPinLock -> AppLockActivity::class.java
            authManager.isLoggedIn -> MainActivity::class.java
            else -> LoginActivity::class.java
        }

        startActivity(Intent(this, destination))
        finish()
    }
}
