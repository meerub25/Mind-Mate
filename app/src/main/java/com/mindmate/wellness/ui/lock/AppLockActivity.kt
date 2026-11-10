package com.mindmate.wellness.ui.lock

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.View
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mindmate.wellness.R
import com.mindmate.wellness.databinding.ActivityAppLockBinding
import com.mindmate.wellness.ui.auth.LoginActivity
import com.mindmate.wellness.ui.profile.ProfileFragment

class AppLockActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_MODE = "extra_mode"
        const val MODE_VERIFY = "mode_verify"
        const val MODE_SETUP = "mode_setup"
    }

    private lateinit var binding: ActivityAppLockBinding
    private lateinit var prefs: SharedPreferences

    private var currentMode = MODE_VERIFY
    private val enteredPin = StringBuilder()
    private var setupFirstPin: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAppLockBinding.inflate(layoutInflater)
        setContentView(binding.root)

        prefs = getSharedPreferences(ProfileFragment.PREFS_NAME, Context.MODE_PRIVATE)
        currentMode = intent.getStringExtra(EXTRA_MODE) ?: MODE_VERIFY

        configureUiForMode()
        setupKeypad()
    }

    private fun configureUiForMode() {
        if (currentMode == MODE_SETUP) {
            binding.tvLockTitle.text = "Set Up App Lock"
            binding.tvLockSubtitle.text = "Choose a 4-digit PIN for MindEase"
            binding.btnForgotPin.visibility = View.GONE
            binding.btnKeyCancel.text = "Cancel"
            binding.btnKeyCancel.setOnClickListener { finish() }
        } else {
            binding.tvLockTitle.text = "MindEase Lock"
            binding.tvLockSubtitle.text = "Enter your 4-digit PIN to open your safe space"
            binding.btnForgotPin.visibility = View.VISIBLE
            binding.btnKeyCancel.text = ""
            binding.btnKeyCancel.isClickable = false

            binding.btnForgotPin.setOnClickListener {
                showForgotPinDialog()
            }
        }
    }

    private fun setupKeypad() {
        val numberButtons = listOf(
            binding.btnKey0 to "0",
            binding.btnKey1 to "1",
            binding.btnKey2 to "2",
            binding.btnKey3 to "3",
            binding.btnKey4 to "4",
            binding.btnKey5 to "5",
            binding.btnKey6 to "6",
            binding.btnKey7 to "7",
            binding.btnKey8 to "8",
            binding.btnKey9 to "9"
        )

        numberButtons.forEach { (button, digit) ->
            button.setOnClickListener {
                appendDigit(digit)
            }
        }

        binding.btnKeyDelete.setOnClickListener {
            if (enteredPin.isNotEmpty()) {
                enteredPin.deleteCharAt(enteredPin.length - 1)
                updateDots()
            }
        }
    }

    private fun appendDigit(digit: String) {
        if (enteredPin.length < 4) {
            enteredPin.append(digit)
            updateDots()

            if (enteredPin.length == 4) {
                handlePinComplete(enteredPin.toString())
            }
        }
    }

    private fun updateDots() {
        val len = enteredPin.length
        binding.dot1.setBackgroundResource(if (len >= 1) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
        binding.dot2.setBackgroundResource(if (len >= 2) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
        binding.dot3.setBackgroundResource(if (len >= 3) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
        binding.dot4.setBackgroundResource(if (len >= 4) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
    }

    private fun handlePinComplete(pin: String) {
        if (currentMode == MODE_SETUP) {
            if (setupFirstPin == null) {
                // First pass complete, ask to confirm
                setupFirstPin = pin
                enteredPin.clear()
                updateDots()
                binding.tvLockSubtitle.text = "Confirm your 4-digit PIN"
            } else {
                // Second pass confirmation
                if (pin == setupFirstPin) {
                    prefs.edit()
                        .putString(ProfileFragment.KEY_PIN, pin)
                        .putBoolean(ProfileFragment.KEY_LOCK_ENABLED, true)
                        .apply()
                    Toast.makeText(this, "App Lock Enabled 🔒", Toast.LENGTH_SHORT).show()
                    setResult(RESULT_OK)
                    finish()
                } else {
                    shakeDots()
                    Toast.makeText(this, "PINs did not match. Please try again.", Toast.LENGTH_SHORT).show()
                    setupFirstPin = null
                    enteredPin.clear()
                    updateDots()
                    binding.tvLockSubtitle.text = "Choose a 4-digit PIN for MindEase"
                }
            }
        } else {
            // VERIFY Mode
            val savedPin = prefs.getString(ProfileFragment.KEY_PIN, "")
            if (pin == savedPin) {
                setResult(RESULT_OK)
                finish()
            } else {
                shakeDots()
                Toast.makeText(this, "Incorrect PIN. Try again.", Toast.LENGTH_SHORT).show()
                enteredPin.clear()
                updateDots()
            }
        }
    }

    private fun shakeDots() {
        val shake = TranslateAnimation(0f, 16f, 0f, 0f).apply {
            duration = 400
            interpolator = CycleInterpolator(3f)
        }
        binding.layoutPinDots.startAnimation(shake)
    }

    private fun showForgotPinDialog() {
        MaterialAlertDialogBuilder(this)
            .setTitle("Forgot PIN?")
            .setMessage("To reset your PIN, you will need to re-login to verify your student identity.")
            .setPositiveButton("Reset & Log In") { _, _ ->
                prefs.edit()
                    .remove(ProfileFragment.KEY_PIN)
                    .putBoolean(ProfileFragment.KEY_LOCK_ENABLED, false)
                    .apply()
                val intent = Intent(this, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
                finish()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
