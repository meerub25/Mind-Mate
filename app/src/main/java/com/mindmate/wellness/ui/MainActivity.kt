package com.mindmate.wellness.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.mindmate.wellness.R
import com.mindmate.wellness.databinding.ActivityMainBinding
import com.mindmate.wellness.ui.chat.AiCompanionFragment
import com.mindmate.wellness.ui.home.HomeFragment
import com.mindmate.wellness.ui.journal.JournalFragment
import com.mindmate.wellness.ui.lock.AppLockActivity
import com.mindmate.wellness.ui.mood.MoodTrackerFragment
import com.mindmate.wellness.ui.more.MoreFragment
import com.mindmate.wellness.ui.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var hasUnlockedThisSession = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkAppLock()
        setupBottomNavigation()

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
        }
    }

    private fun checkAppLock() {
        val prefs = getSharedPreferences(ProfileFragment.PREFS_NAME, Context.MODE_PRIVATE)
        val isLockEnabled = prefs.getBoolean(ProfileFragment.KEY_LOCK_ENABLED, false)
        val savedPin = prefs.getString(ProfileFragment.KEY_PIN, null)

        if (isLockEnabled && !savedPin.isNullOrEmpty() && !hasUnlockedThisSession) {
            val intent = Intent(this, AppLockActivity::class.java).apply {
                putExtra(AppLockActivity.EXTRA_MODE, AppLockActivity.MODE_VERIFY)
            }
            startActivity(intent)
            hasUnlockedThisSession = true
        }
    }

    private fun setupBottomNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            val fragment: Fragment = when (item.itemId) {
                R.id.nav_home -> HomeFragment()
                R.id.nav_chat -> AiCompanionFragment()
                R.id.nav_mood -> MoodTrackerFragment()
                R.id.nav_journal -> JournalFragment()
                R.id.nav_more -> MoreFragment()
                else -> HomeFragment()
            }
            loadFragment(fragment)
            true
        }
    }

    fun selectNavigationTab(menuId: Int) {
        binding.bottomNavigation.selectedItemId = menuId
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }
}
