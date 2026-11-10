package com.mindmate.wellness.ui.auth

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mindmate.wellness.data.firebase.FirebaseAuthManager
import com.mindmate.wellness.databinding.ActivityForgotPasswordBinding

class ForgotPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityForgotPasswordBinding
    private val authManager = FirebaseAuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendResetLink.setOnClickListener {
            val email = binding.etResetEmail.text?.toString()?.trim() ?: ""
            if (email.isEmpty()) {
                binding.etResetEmail.error = "Please enter your email"
                return@setOnClickListener
            }

            binding.btnSendResetLink.isEnabled = false
            binding.progressReset.visibility = View.VISIBLE

            authManager.resetPassword(
                email = email,
                onSuccess = {
                    binding.progressReset.visibility = View.GONE
                    binding.btnSendResetLink.isEnabled = true
                    Toast.makeText(this, "Recovery link sent to $email. Please check your inbox.", Toast.LENGTH_LONG).show()
                    finish()
                },
                onError = { err ->
                    binding.progressReset.visibility = View.GONE
                    binding.btnSendResetLink.isEnabled = true
                    Toast.makeText(this, err, Toast.LENGTH_LONG).show()
                }
            )
        }

        binding.btnBackToSignIn.setOnClickListener {
            finish()
        }
    }
}
