package com.mindmate.wellness.ui.onboarding

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.mindmate.wellness.R
import com.mindmate.wellness.databinding.ActivityOnboardingBinding
import com.mindmate.wellness.ui.auth.LoginActivity

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    data class SlideItem(
        val emoji: String,
        val title: String,
        val desc: String,
        val showPrivacyPromise: Boolean = false
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val slides = listOf(
            SlideItem(
                emoji = "💬",
                title = getString(R.string.onboarding_slide1_title),
                desc = getString(R.string.onboarding_slide1_desc)
            ),
            SlideItem(
                emoji = "📈",
                title = getString(R.string.onboarding_slide2_title),
                desc = getString(R.string.onboarding_slide2_desc)
            ),
            SlideItem(
                emoji = "🌱",
                title = getString(R.string.onboarding_slide3_title),
                desc = getString(R.string.onboarding_slide3_desc),
                showPrivacyPromise = true
            )
        )

        val adapter = OnboardingAdapter(slides)
        binding.viewPagerOnboarding.adapter = adapter

        binding.viewPagerOnboarding.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateDots(position)
                if (position == slides.size - 1) {
                    binding.btnOnboardingAction.text = getString(R.string.onboarding_get_started)
                } else {
                    binding.btnOnboardingAction.text = getString(R.string.onboarding_next)
                }
            }
        })

        binding.btnOnboardingAction.setOnClickListener {
            val current = binding.viewPagerOnboarding.currentItem
            if (current < slides.size - 1) {
                binding.viewPagerOnboarding.currentItem = current + 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnOnboardingSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun updateDots(position: Int) {
        binding.dot1.setBackgroundResource(if (position == 0) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
        binding.dot2.setBackgroundResource(if (position == 1) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
        binding.dot3.setBackgroundResource(if (position == 2) R.drawable.bg_dot_active else R.drawable.bg_dot_inactive)
    }

    private fun finishOnboarding() {
        val prefs = getSharedPreferences("mindease_prefs", Context.MODE_PRIVATE)
        prefs.edit().putBoolean("has_completed_onboarding", true).apply()

        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    class OnboardingAdapter(private val items: List<SlideItem>) : RecyclerView.Adapter<OnboardingAdapter.SlideViewHolder>() {
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SlideViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_onboarding_slide, parent, false)
            return SlideViewHolder(view)
        }

        override fun onBindViewHolder(holder: SlideViewHolder, position: Int) {
            val item = items[position]
            holder.tvEmoji.text = item.emoji
            holder.tvTitle.text = item.title
            holder.tvDesc.text = item.desc
            holder.tvPrivacy.visibility = if (item.showPrivacyPromise) View.VISIBLE else View.GONE
        }

        override fun getItemCount(): Int = items.size

        class SlideViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvEmoji: TextView = itemView.findViewById(R.id.tvOnboardingEmoji)
            val tvTitle: TextView = itemView.findViewById(R.id.tvOnboardingTitle)
            val tvDesc: TextView = itemView.findViewById(R.id.tvOnboardingDesc)
            val tvPrivacy: TextView = itemView.findViewById(R.id.tvOnboardingPrivacyPromise)
        }
    }
}
