package com.mindmate.wellness.ui.profile

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.mindmate.wellness.R
import com.mindmate.wellness.data.firebase.FirebaseAuthManager
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.databinding.FragmentProfileBinding
import com.mindmate.wellness.ui.auth.LoginActivity
import com.mindmate.wellness.ui.lock.AppLockActivity
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MindmateDatabaseHelper
    private lateinit var authManager: FirebaseAuthManager
    private lateinit var prefs: SharedPreferences

    companion object {
        const val PREFS_NAME = "mindease_prefs"
        const val KEY_NICKNAME = "user_nickname"
        const val KEY_CAMPUS = "user_campus"
        const val KEY_LANGUAGE = "app_language"
        const val KEY_PIN = "app_pin"
        const val KEY_LOCK_ENABLED = "app_lock_enabled"
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MindmateDatabaseHelper(requireContext())
        authManager = FirebaseAuthManager()
        prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

        loadProfileData()
        loadStats()
        setupListeners()
    }

    private fun loadProfileData() {
        val nickname = prefs.getString(KEY_NICKNAME, null)
            ?: authManager.currentUser?.displayName
            ?: "Student"
        val campus = prefs.getString(KEY_CAMPUS, "University Student • Pakistan")
        val language = prefs.getString(KEY_LANGUAGE, "English / Urdu / Roman Urdu (Auto)")
        val isLockEnabled = prefs.getBoolean(KEY_LOCK_ENABLED, false)

        binding.tvProfileNickname.text = nickname
        binding.tvProfileUniversity.text = campus
        binding.tvCurrentLanguage.text = language
        binding.switchAppLock.isChecked = isLockEnabled

        val initial = if (nickname.isNotBlank()) nickname.trim().first().uppercaseChar().toString() else "M"
        binding.tvProfileAvatarInitial.text = initial
    }

    private fun loadStats() {
        val checkins = dbHelper.getAllMoodEntries().size
        val journals = dbHelper.getAllJournalEntries().size
        val avgStress = dbHelper.getAverageStressLevel()

        binding.tvStatCheckinsCount.text = checkins.toString()
        binding.tvStatJournalsCount.text = journals.toString()
        binding.tvStatAvgStress.text = String.format(Locale.US, "%.1f", avgStress)
    }

    private fun setupListeners() {
        binding.rowEditNickname.setOnClickListener {
            showEditProfileDialog()
        }

        binding.rowLanguage.setOnClickListener {
            showLanguageDialog()
        }

        binding.switchAppLock.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                val existingPin = prefs.getString(KEY_PIN, null)
                if (existingPin.isNullOrEmpty()) {
                    // Open PIN Setup
                    val intent = Intent(requireContext(), AppLockActivity::class.java).apply {
                        putExtra(AppLockActivity.EXTRA_MODE, AppLockActivity.MODE_SETUP)
                    }
                    startActivity(intent)
                } else {
                    prefs.edit().putBoolean(KEY_LOCK_ENABLED, true).apply()
                    Toast.makeText(requireContext(), "App Lock Enabled 🔒", Toast.LENGTH_SHORT).show()
                }
            } else {
                prefs.edit().putBoolean(KEY_LOCK_ENABLED, false).apply()
                Toast.makeText(requireContext(), "App Lock Disabled", Toast.LENGTH_SHORT).show()
            }
        }

        binding.rowLogout.setOnClickListener {
            showLogoutConfirmation()
        }

        binding.rowDeleteAllData.setOnClickListener {
            showDeleteDataConfirmation()
        }
    }

    private fun showEditProfileDialog() {
        val ctx = requireContext()
        val layout = LinearLayout(ctx).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val etNickname = EditText(ctx).apply {
            hint = "Nickname (e.g. Ali)"
            setText(prefs.getString(KEY_NICKNAME, ""))
        }
        val etCampus = EditText(ctx).apply {
            hint = "University & Campus (e.g. NUST Islamabad)"
            setText(prefs.getString(KEY_CAMPUS, ""))
        }

        layout.addView(etNickname)
        layout.addView(etCampus)

        MaterialAlertDialogBuilder(ctx)
            .setTitle("✏️ Edit Profile")
            .setView(layout)
            .setPositiveButton("Save") { _, _ ->
                val newNick = etNickname.text.toString().trim()
                val newCampus = etCampus.text.toString().trim()

                prefs.edit()
                    .putString(KEY_NICKNAME, if (newNick.isNotEmpty()) newNick else "Student")
                    .putString(KEY_CAMPUS, if (newCampus.isNotEmpty()) newCampus else "University Student")
                    .apply()

                loadProfileData()
                Toast.makeText(ctx, "Profile updated ✨", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showLanguageDialog() {
        val languages = arrayOf(
            "Auto (English, Urdu, Roman Urdu)",
            "English",
            "اردو (Urdu)",
            "Roman Urdu (Urdu in English alphabet)"
        )

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🌐 Companion Language")
            .setItems(languages) { _, which ->
                val selected = languages[which]
                prefs.edit().putString(KEY_LANGUAGE, selected).apply()
                binding.tvCurrentLanguage.text = selected
                Toast.makeText(requireContext(), "Language set to $selected", Toast.LENGTH_SHORT).show()
            }
            .setNegativeButton("Close", null)
            .show()
    }

    private fun showLogoutConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🚪 Log Out")
            .setMessage("Are you sure you want to log out of MindEase?")
            .setPositiveButton("Log Out") { _, _ ->
                authManager.signOut()
                val intent = Intent(requireContext(), LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun showDeleteDataConfirmation() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("🗑️ Delete All My Data?")
            .setMessage(R.string.profile_delete_confirm)
            .setPositiveButton("Yes, Delete All") { _, _ ->
                dbHelper.clearAllData()
                loadStats()
                Toast.makeText(requireContext(), "All local data has been securely wiped.", Toast.LENGTH_LONG).show()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadStats()
        loadProfileData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
