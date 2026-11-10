package com.mindmate.wellness.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mindmate.wellness.data.firebase.FirebaseAuthManager
import com.mindmate.wellness.databinding.ActivityLoginBinding
import com.mindmate.wellness.ui.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val authManager = FirebaseAuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check if already logged in -> Go straight to main app
        if (authManager.isLoggedIn) {
            goToMainActivity()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSignIn.setOnClickListener {
            val email = binding.etLoginEmail.text?.toString()?.trim() ?: ""
            val pass = binding.etLoginPassword.text?.toString()?.trim() ?: ""

            if (email.isEmpty()) {
                binding.etLoginEmail.error = "Please enter your email"
                return@setOnClickListener
            }
            if (pass.isEmpty()) {
                binding.etLoginPassword.error = "Please enter your password"
                return@setOnClickListener
            }

            setLoading(true)
            authManager.login(
                email = email,
                pass = pass,
                onSuccess = {
                    setLoading(false)
                    Toast.makeText(this, "Welcome back to Mindmate!", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                },
                onError = { err ->
                    setLoading(false)
                    Toast.makeText(this, err, Toast.LENGTH_LONG).show()
                }
            )
        }

        binding.tvGoToSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.tvForgotPassword.setOnClickListener {
            startActivity(Intent(this, ForgotPasswordActivity::class.java))
        }

        binding.btnGuestMode.setOnClickListener {
            setLoading(true)
            authManager.signInAnonymously(
                onSuccess = {
                    setLoading(false)
                    Toast.makeText(this, "Continuing as private guest.", Toast.LENGTH_SHORT).show()
                    goToMainActivity()
                },
                onError = {
                    setLoading(false)
                    // Even if network fails, allow local-only guest session!
                    goToMainActivity()
                }
            )
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnSignIn.isEnabled = !loading
        binding.btnGuestMode.isEnabled = !loading
        binding.progressLogin.visibility = if (loading) View.VISIBLE else View.GONE
    }

    private fun goToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}
