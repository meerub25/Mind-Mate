package com.mindmate.wellness.ui.journal

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.mindmate.wellness.R
import com.mindmate.wellness.data.local.JournalEntry
import com.mindmate.wellness.data.local.MindmateDatabaseHelper
import com.mindmate.wellness.databinding.FragmentJournalBinding

class JournalFragment : Fragment() {

    private var _binding: FragmentJournalBinding? = null
    private val binding get() = _binding!!

    private lateinit var dbHelper: MindmateDatabaseHelper
    private lateinit var adapter: JournalAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentJournalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dbHelper = MindmateDatabaseHelper(requireContext())

        adapter = JournalAdapter { entry ->
            confirmDelete(entry)
        }

        binding.rvJournalEntries.layoutManager = LinearLayoutManager(requireContext())
        binding.rvJournalEntries.adapter = adapter

        binding.btnSaveJournal.setOnClickListener {
            val text = binding.etJournalInput.text?.toString()?.trim() ?: ""
            if (text.isNotEmpty()) {
                dbHelper.insertJournalEntry(text)
                binding.etJournalInput.setText("")
                Toast.makeText(requireContext(), "Entry saved privately 🔒", Toast.LENGTH_SHORT).show()
                loadEntries()
            }
        }

        loadEntries()
    }

    private fun loadEntries() {
        val entries = dbHelper.getAllJournalEntries()
        adapter.submitList(entries)
        if (entries.isEmpty()) {
            binding.tvEmptyJournal.visibility = View.VISIBLE
            binding.rvJournalEntries.visibility = View.GONE
        } else {
            binding.tvEmptyJournal.visibility = View.GONE
            binding.rvJournalEntries.visibility = View.VISIBLE
        }
    }

    private fun confirmDelete(entry: JournalEntry) {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Entry")
            .setMessage("Do you want to remove this journal entry?")
            .setPositiveButton("Delete") { d, _ ->
                dbHelper.deleteJournalEntry(entry.id)
                loadEntries()
                d.dismiss()
            }
            .setNegativeButton("Cancel") { d, _ -> d.dismiss() }
            .show()
    }

    override fun onResume() {
        super.onResume()
        loadEntries()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    class JournalAdapter(
        private val onDelete: (JournalEntry) -> Unit
    ) : RecyclerView.Adapter<JournalAdapter.JournalViewHolder>() {

        private val items = mutableListOf<JournalEntry>()

        fun submitList(newItems: List<JournalEntry>) {
            items.clear()
            items.addAll(newItems)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JournalViewHolder {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.item_journal_entry, parent, false)
            return JournalViewHolder(view)
        }

        override fun onBindViewHolder(holder: JournalViewHolder, position: Int) {
            val item = items[position]
            holder.tvDate.text = item.dateString
            holder.tvContent.text = item.content
            holder.btnDelete.setOnClickListener { onDelete(item) }
        }

        override fun getItemCount(): Int = items.size

        class JournalViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvDate: TextView = itemView.findViewById(R.id.tvJournalDate)
            val tvContent: TextView = itemView.findViewById(R.id.tvJournalContent)
            val btnDelete: MaterialButton = itemView.findViewById(R.id.btnDeleteJournal)
        }
    }
}
