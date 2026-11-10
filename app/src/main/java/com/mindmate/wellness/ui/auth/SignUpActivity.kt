package com.mindmate.wellness.ui.auth

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.mindmate.wellness.data.firebase.FirebaseAuthManager
import com.mindmate.wellness.databinding.ActivitySignupBinding
import com.mindmate.wellness.ui.MainActivity

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignupBinding
    private val authManager = FirebaseAuthManager()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupListeners()
    }

    private fun setupListeners() {
        binding.btnSignUpSubmit.setOnClickListener {
            val name = binding.etSignUpName.text?.toString()?.trim() ?: ""
            val email = binding.etSignUpEmail.text?.toString()?.trim() ?: ""
            val pass = binding.etSignUpPassword.text?.toString()?.trim() ?: ""

            if (name.isEmpty()) {
                binding.etSignUpName.error = "Name is required"
                return@setOnClickListener
            }
            if (email.isEmpty()) {
                binding.etSignUpEmail.error = "Email is required"
                return@setOnClickListener
            }
            if (pass.length < 6) {
                binding.etSignUpPassword.error = "Password must be at least 6 characters"
                return@setOnClickListener
            }

            val finalUni = "University Student"

            setLoading(true)
            authManager.signUp(
                name = name,
                university = finalUni,
                email = email,
                pass = pass,
                onSuccess = {
                    setLoading(false)
                    Toast.makeText(this, "Account created! Welcome, $name.", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java).apply {
                        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    }
                    startActivity(intent)
                    finish()
                },
                onError = { err ->
                    setLoading(false)
                    Toast.makeText(this, err, Toast.LENGTH_LONG).show()
                }
            )
        }

        binding.tvGoToSignIn.setOnClickListener {
            finish()
        }
    }

    private fun setLoading(loading: Boolean) {
        binding.btnSignUpSubmit.isEnabled = !loading
        binding.progressSignUp.visibility = if (loading) View.VISIBLE else View.GONE
    }
}
