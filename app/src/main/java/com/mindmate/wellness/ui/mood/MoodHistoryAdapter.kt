package com.mindmate.wellness.ui.mood

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.mindmate.wellness.R
import com.mindmate.wellness.data.model.MoodEntry

class MoodHistoryAdapter : RecyclerView.Adapter<MoodHistoryAdapter.MoodViewHolder>() {

    private val entries = mutableListOf<MoodEntry>()

    fun submitList(newList: List<MoodEntry>) {
        entries.clear()
        entries.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_mood_entry, parent, false)
        return MoodViewHolder(view)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        val item = entries[position]
        holder.tvEmoji.text = item.moodEmoji
        holder.tvLabel.text = item.moodLabel
        holder.tvDate.text = item.dateString
        holder.tvStress.text = "Stress: ${item.stressLevel}/10"

        if (item.journalNote.isNotBlank()) {
            holder.tvNote.visibility = View.VISIBLE
            holder.tvNote.text = "\"${item.journalNote}\""
        } else {
            holder.tvNote.visibility = View.GONE
        }

        holder.chipGroup.removeAllViews()
        for (trigger in item.triggers.take(4)) {
            val chip = Chip(holder.itemView.context).apply {
                text = trigger
                isClickable = false
                isCheckable = false
                textSize = 11f
            }
            holder.chipGroup.addView(chip)
        }
    }

    override fun getItemCount(): Int = entries.size

    class MoodViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvEmoji: TextView = itemView.findViewById(R.id.tvMoodEmoji)
        val tvLabel: TextView = itemView.findViewById(R.id.tvMoodLabel)
        val tvDate: TextView = itemView.findViewById(R.id.tvMoodDate)
        val tvStress: TextView = itemView.findViewById(R.id.tvStressRating)
        val tvNote: TextView = itemView.findViewById(R.id.tvJournalSnippet)
        val chipGroup: ChipGroup = itemView.findViewById(R.id.chipGroupTriggers)
    }
}
