package com.mindmate.wellness.ui.resources

import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mindmate.wellness.R
import com.mindmate.wellness.data.model.StudentResource

class ResourcesAdapter(
    private val resources: List<StudentResource>
) : RecyclerView.Adapter<ResourcesAdapter.ResourceViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ResourceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_resource_card, parent, false)
        return ResourceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ResourceViewHolder, position: Int) {
        val item = resources[position]
        holder.tvName.text = item.name
        holder.tvCategory.text = item.category
        holder.tvDescription.text = item.description
        holder.tvHours.text = if (item.is24Hours) "🕒 24/7 Free Helpline" else "🕒 ${item.operatingHours}"
        holder.tvPhone.text = "📞 ${item.contactNumber}"

        holder.btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL).apply {
                data = Uri.parse("tel:${item.contactNumber.replace("-", "")}")
            }
            holder.itemView.context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = resources.size

    class ResourceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tvResourceName)
        val tvCategory: TextView = itemView.findViewById(R.id.tvResourceCategory)
        val tvDescription: TextView = itemView.findViewById(R.id.tvResourceDesc)
        val tvHours: TextView = itemView.findViewById(R.id.tvResourceHours)
        val tvPhone: TextView = itemView.findViewById(R.id.tvResourcePhone)
        val btnCall: MaterialButton = itemView.findViewById(R.id.btnCallHelpline)
    }
}
