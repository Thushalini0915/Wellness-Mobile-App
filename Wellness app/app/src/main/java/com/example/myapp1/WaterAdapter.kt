package com.example.myapp1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp1.databinding.ItemWaterEntryBinding
import java.text.SimpleDateFormat
import java.util.*

class WaterAdapter(
    private val onDeleteClick: (WaterEntry) -> Unit
) : ListAdapter<WaterEntry, WaterAdapter.WaterViewHolder>(WaterDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WaterViewHolder {
        val binding = ItemWaterEntryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return WaterViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WaterViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class WaterViewHolder(
        private val binding: ItemWaterEntryBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        private val timeFormat = SimpleDateFormat("h:mm a", Locale.getDefault())

        fun bind(waterEntry: WaterEntry) {
            binding.apply {
                tvAmount.text = "${waterEntry.amount}ml"
                tvTime.text = timeFormat.format(waterEntry.date)
                
                if (waterEntry.note.isNotEmpty()) {
                    tvNote.text = waterEntry.note
                    tvNote.visibility = android.view.View.VISIBLE
                } else {
                    tvNote.visibility = android.view.View.GONE
                }
                
                // Set delete button click listener
                btnDelete.setOnClickListener {
                    onDeleteClick(waterEntry)
                }
            }
        }
    }
}

private class WaterDiffCallback : DiffUtil.ItemCallback<WaterEntry>() {
    override fun areItemsTheSame(oldItem: WaterEntry, newItem: WaterEntry): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: WaterEntry, newItem: WaterEntry): Boolean {
        return oldItem == newItem
    }
}
