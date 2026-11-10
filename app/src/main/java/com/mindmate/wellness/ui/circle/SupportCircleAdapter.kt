package com.mindmate.wellness.ui.circle

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.mindmate.wellness.data.local.SupportCirclePost
import com.mindmate.wellness.databinding.ItemCirclePostBinding
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SupportCircleAdapter(
    private var posts: MutableList<SupportCirclePost>,
    private val onMeTooClicked: (SupportCirclePost) -> Unit
) : RecyclerView.Adapter<SupportCircleAdapter.CircleViewHolder>() {

    private val likedIds = mutableSetOf<Long>()

    inner class CircleViewHolder(val binding: ItemCirclePostBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CircleViewHolder {
        val binding = ItemCirclePostBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CircleViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CircleViewHolder, position: Int) {
        val post = posts[position]
        with(holder.binding) {
            tvCircleAlias.text = post.alias
            tvCircleContent.text = post.content
            tvCircleTime.text = formatRelativeTime(post.timestamp)

            // Select emoji from alias
            tvCircleAvatar.text = when {
                post.alias.contains("Owl", ignoreCase = true) -> "🦉"
                post.alias.contains("Falcon", ignoreCase = true) -> "🦅"
                post.alias.contains("Pine", ignoreCase = true) -> "🌲"
                post.alias.contains("Sparrow", ignoreCase = true) -> "🐦"
                post.alias.contains("Fox", ignoreCase = true) -> "🦊"
                post.alias.contains("River", ignoreCase = true) -> "🌊"
                post.alias.contains("Star", ignoreCase = true) -> "⭐"
                else -> "🌱"
            }

            val isLiked = likedIds.contains(post.id)
            btnMeToo.text = if (isLiked) "💜 Me too (${post.meTooCount})" else "🤍 Me too (${post.meTooCount})"

            btnMeToo.setOnClickListener {
                if (!likedIds.contains(post.id)) {
                    likedIds.add(post.id)
                    post.meTooCount += 1
                    btnMeToo.text = "💜 Me too (${post.meTooCount})"
                    btnMeToo.animate().scaleX(1.1f).scaleY(1.1f).setDuration(120).withEndAction {
                        btnMeToo.animate().scaleX(1.0f).scaleY(1.0f).setDuration(120).start()
                    }.start()
                    onMeTooClicked(post)
                }
            }
        }
    }

    override fun getItemCount(): Int = posts.size

    fun updateList(newPosts: List<SupportCirclePost>) {
        posts = newPosts.toMutableList()
        notifyDataSetChanged()
    }

    private fun formatRelativeTime(timestamp: Long): String {
        val now = System.currentTimeMillis()
        val diff = (now - timestamp).coerceAtLeast(0)
        val minutes = diff / (1000 * 60)
        val hours = minutes / 60
        val days = hours / 24

        return when {
            minutes < 2 -> "Just now"
            minutes < 60 -> "${minutes}m ago"
            hours < 24 -> "${hours}h ago"
            days == 1L -> "Yesterday"
            days < 7 -> "${days}d ago"
            else -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(timestamp))
        }
    }
}
