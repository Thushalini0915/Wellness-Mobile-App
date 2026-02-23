package com.example.myapp1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp1.databinding.ItemMoodEntryBinding
import java.text.SimpleDateFormat
import java.util.*

class MoodAdapter(
    private val onEditClick: (MoodEntry) -> Unit,
    private val onDeleteClick: (MoodEntry) -> Unit
) : ListAdapter<MoodEntry, MoodAdapter.MoodViewHolder>(MoodDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MoodViewHolder {
        val binding = ItemMoodEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return MoodViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MoodViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class MoodViewHolder(
        private val binding: ItemMoodEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val dateFormat = SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
        private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        fun bind(moodEntry: MoodEntry) {
            binding.apply {
                emojiTextView.text = moodEntry.moodEmoji
                chipMoodType.text = moodEntry.moodType
                noteTextView.text = if (moodEntry.note.isNotEmpty()) moodEntry.note else "No note added"
                dateTextView.text = dateFormat.format(moodEntry.date)
                timeTextView.text = timeFormat.format(moodEntry.date)
                
                // Set action button click listeners
                btnEdit.setOnClickListener {
                    onEditClick(moodEntry)
                }
                
                btnDelete.setOnClickListener {
                    onDeleteClick(moodEntry)
                }
            }
        }
    }
}

private class MoodDiffCallback : DiffUtil.ItemCallback<MoodEntry>() {
    override fun areItemsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: MoodEntry, newItem: MoodEntry): Boolean {
        return oldItem == newItem
    }
}
