package com.example.myapp1

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.myapp1.databinding.ItemHabitBinding

class HabitsAdapter(
    private val onHabitAction: (Habit, HabitAction) -> Unit
) : ListAdapter<Habit, HabitsAdapter.HabitViewHolder>(HabitDiffCallback()) {
    
    private class HabitDiffCallback : DiffUtil.ItemCallback<Habit>() {
        override fun areItemsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Habit, newItem: Habit): Boolean {
            return oldItem == newItem
        }
    }

    inner class HabitViewHolder(
        private val binding: ItemHabitBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(habit: Habit) {
            binding.apply {
                // Set habit data
                tvHabitName.text = habit.name
                tvProgress.text = "${habit.completedCount}/${habit.targetCount}"
                progressBar.max = habit.targetCount
                progressBar.progress = habit.completedCount
                chipFrequency.text = habit.frequency
                
                // Set completion state
                cardView.isChecked = habit.isCompleted
                
                // Update toggle button appearance
                if (habit.isCompleted) {
                    btnToggle.text = "✓"
                    btnToggle.setBackgroundColor(itemView.context.getColor(android.R.color.holo_green_light))
                } else {
                    btnToggle.text = ""
                    btnToggle.setBackgroundColor(itemView.context.getColor(android.R.color.transparent))
                }
                
                // Set click listeners
                cardView.setOnClickListener {
                    onHabitAction(habit, HabitAction.TOGGLE)
                }
                
                btnToggle.setOnClickListener {
                    onHabitAction(habit, HabitAction.TOGGLE)
                }
                
                btnEdit.setOnClickListener {
                    onHabitAction(habit, HabitAction.EDIT)
                }
                
                btnDelete.setOnClickListener {
                    onHabitAction(habit, HabitAction.DELETE)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val binding = ItemHabitBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HabitViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
    
    override fun getItemViewType(position: Int): Int {
        return position
    }
}