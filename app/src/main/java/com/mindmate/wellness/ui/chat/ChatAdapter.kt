package com.mindmate.wellness.ui.chat

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mindmate.wellness.R
import com.mindmate.wellness.data.model.ChatMessage
import com.mindmate.wellness.data.model.MessageSender

class ChatAdapter(
    private val onActionClicked: (String) -> Unit
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private val messages = mutableListOf<ChatMessage>()

    companion object {
        private const val TYPE_USER = 1
        private const val TYPE_AI = 2
    }

    fun submitList(newMessages: List<ChatMessage>) {
        messages.clear()
        messages.addAll(newMessages)
        notifyDataSetChanged()
    }

    fun addMessage(msg: ChatMessage) {
        messages.add(msg)
        notifyItemInserted(messages.size - 1)
    }

    override fun getItemViewType(position: Int): Int {
        return if (messages[position].sender == MessageSender.USER) TYPE_USER else TYPE_AI
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == TYPE_USER) {
            val view = inflater.inflate(R.layout.item_chat_user, parent, false)
            UserViewHolder(view)
        } else {
            val view = inflater.inflate(R.layout.item_chat_ai, parent, false)
            AiViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val msg = messages[position]
        if (holder is UserViewHolder) {
            holder.tvContent.text = msg.content
        } else if (holder is AiViewHolder) {
            holder.tvContent.text = msg.content
            if (!msg.suggestedAction.isNullOrEmpty()) {
                holder.btnAction.visibility = View.VISIBLE
                when (msg.suggestedAction) {
                    "BREATHING" -> holder.btnAction.text = "🌬️ Try 2-Min Box Breathing"
                    "GROUNDING" -> holder.btnAction.text = "🧘 5-4-3-2-1 Grounding Tool"
                    "HELPLINES" -> holder.btnAction.text = "📞 View Verified Helplines"
                    else -> holder.btnAction.text = "✨ Open Coping Tool"
                }
                holder.btnAction.setOnClickListener {
                    onActionClicked(msg.suggestedAction)
                }
            } else {
                holder.btnAction.visibility = View.GONE
            }
        }
    }

    override fun getItemCount(): Int = messages.size

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContent: TextView = itemView.findViewById(R.id.tvUserMessage)
    }

    class AiViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvContent: TextView = itemView.findViewById(R.id.tvAiMessage)
        val btnAction: MaterialButton = itemView.findViewById(R.id.btnSuggestedAction)
    }
}
