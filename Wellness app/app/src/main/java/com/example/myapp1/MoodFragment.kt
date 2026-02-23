package com.example.myapp1

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import com.example.myapp1.databinding.FragmentMoodBinding
import com.google.android.material.snackbar.Snackbar
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Locale

class MoodFragment : Fragment() {

    private var _binding: FragmentMoodBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: MoodViewModel by activityViewModels()
    private lateinit var moodAdapter: MoodAdapter
    private val moodEntries = mutableListOf<MoodEntry>()
    
    // Emoji options for mood selection
    private val emojiOptions = listOf(
        "😊" to "Happy",
        "😢" to "Sad",
        "😡" to "Angry",
        "😴" to "Tired",
        "😊" to "Content"
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMoodBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        loadMoodEntries()
        
        // Ensure FAB is visible and clickable
        binding.fabAddMood.visibility = View.VISIBLE
        binding.fabAddMood.setOnClickListener {
            showAddMoodDialog()
        }

        // Share mood summary
        binding.btnShareMood.setOnClickListener {
            shareMoodSummary()
        }
    }
    
    private fun setupRecyclerView() {
        moodAdapter = MoodAdapter(
            onEditClick = { moodEntry ->
                showEditMoodDialog(moodEntry)
            },
            onDeleteClick = { moodEntry ->
                viewModel.deleteMoodEntry(moodEntry)
            }
        )
        
        binding.recyclerMoodEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = moodAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun loadMoodEntries() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.moodEntries.collect { entries ->
                moodEntries.clear()
                moodEntries.addAll(entries)
                moodAdapter.submitList(moodEntries.toList())
                
                // Update mood stats
                updateMoodStats(entries)
                
                // Show empty state if no mood entries
                if (moodEntries.isEmpty()) {
                    binding.recyclerMoodEntries.visibility = View.GONE
                    binding.emptyState.root.visibility = View.VISIBLE
                    binding.moodStatsCard.visibility = View.GONE
                } else {
                    binding.recyclerMoodEntries.visibility = View.VISIBLE
                    binding.emptyState.root.visibility = View.GONE
                    binding.moodStatsCard.visibility = View.VISIBLE
                }
            }
        }
    }
    
    private fun updateMoodStats(entries: List<MoodEntry>) {
        // Update today's mood
        val today = Calendar.getInstance()
        val todayEntries = entries.filter { entry ->
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = entry.date
            today.get(Calendar.YEAR) == entryCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == entryCalendar.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == entryCalendar.get(Calendar.DAY_OF_MONTH)
        }
        
        if (todayEntries.isNotEmpty()) {
            binding.tvTodayMood.text = todayEntries.first().moodEmoji
        } else {
            binding.tvTodayMood.text = "😐"
        }
        
        // Update streak (simplified - just count consecutive days with entries)
        val streak = calculateMoodStreak(entries)
        binding.tvMoodStreak.text = streak.toString()
        
        // Update total entries
        binding.tvTotalEntries.text = entries.size.toString()
    }
    
    private fun calculateMoodStreak(entries: List<MoodEntry>): Int {
        if (entries.isEmpty()) return 0
        
        val calendar = Calendar.getInstance()
        var streak = 0
        val sortedEntries = entries.sortedByDescending { it.date }
        
        for (entry in sortedEntries) {
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = entry.date
            
            if (calendar.get(Calendar.YEAR) == entryCalendar.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == entryCalendar.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == entryCalendar.get(Calendar.DAY_OF_MONTH)) {
                streak++
                calendar.add(Calendar.DAY_OF_MONTH, -1)
            } else {
                break
            }
        }
        
        return streak
    }

    private fun buildMoodSummaryText(): String {
        val entries = moodEntries.toList()
        val todayEmoji = binding.tvTodayMood.text.toString()
        val streakText = binding.tvMoodStreak.text.toString()
        val total = entries.size
        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        val recent = entries
            .sortedByDescending { it.date }
            .take(5)
            .joinToString(separator = "\n") { entry ->
                val notePart = if (entry.note.isNullOrBlank()) "" else ": ${entry.note}"
                "${sdf.format(entry.date)} - ${entry.moodEmoji} ${entry.moodType}${notePart}"
            }

        return buildString {
            appendLine("My Mood Summary")
            appendLine()
            appendLine("Today's Mood: ${todayEmoji}")
            appendLine("Streak: ${streakText} day(s)")
            appendLine("Total Entries: ${total}")
            if (recent.isNotBlank()) {
                appendLine()
                appendLine("Recent entries:")
                appendLine(recent)
            }
        }
    }

    private fun shareMoodSummary() {
        val summaryText = buildMoodSummaryText()
        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_SUBJECT, "My Mood Summary")
            putExtra(Intent.EXTRA_TEXT, summaryText)
        }
        startActivity(Intent.createChooser(shareIntent, "Share mood summary"))
    }
    
    private fun showAddMoodDialog() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_mood_entry, null)
        val emojiGroup = dialogView.findViewById<android.widget.RadioGroup>(R.id.emojiGroup)
        val noteInput = dialogView.findViewById<EditText>(R.id.noteInput)
        
        // Set up radio buttons for emoji selection
        emojiOptions.forEachIndexed { index, (emoji, _) ->
            val radioButton = android.widget.RadioButton(requireContext()).apply {
                text = emoji
                id = index
                textSize = 24f
                setPadding(16, 8, 16, 8)
            }
            emojiGroup.addView(radioButton)
        }
        
        // Select first emoji by default
        if (emojiGroup.childCount > 0) {
            (emojiGroup.getChildAt(0) as? android.widget.RadioButton)?.isChecked = true
        }
        
        AlertDialog.Builder(requireContext())
            .setTitle("How are you feeling?")
            .setView(dialogView)
            .setPositiveButton("Save") { _, _ ->
                val selectedEmojiIndex = emojiGroup.checkedRadioButtonId
                if (selectedEmojiIndex != -1) {
                    val selectedEmoji = emojiOptions[selectedEmojiIndex].first
                    val note = noteInput.text.toString().trim()
                    
                    viewModel.saveMoodEntry(
                        moodType = emojiOptions[selectedEmojiIndex].second,
                        moodEmoji = selectedEmoji,
                        note = note
                    )
                    
                    // Show confirmation
                    Snackbar.make(
                        binding.root,
                        "Mood saved: ${emojiOptions[selectedEmojiIndex].second}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun showEditMoodDialog(moodEntry: MoodEntry) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_mood_entry, null)
        val emojiGroup = dialogView.findViewById<android.widget.RadioGroup>(R.id.emojiGroup)
        val noteInput = dialogView.findViewById<EditText>(R.id.noteInput)
        
        // Set up radio buttons for emoji selection
        emojiOptions.forEachIndexed { index, (emoji, moodType) ->
            val radioButton = android.widget.RadioButton(requireContext()).apply {
                text = emoji
                id = index
                textSize = 24f
                setPadding(16, 8, 16, 8)
                // Check if this is the current mood
                isChecked = moodType == moodEntry.moodType
            }
            emojiGroup.addView(radioButton)
        }
        
        // Set the current note
        noteInput.setText(moodEntry.note)
        
        AlertDialog.Builder(requireContext())
            .setTitle("Edit Mood Entry")
            .setView(dialogView)
            .setPositiveButton("Update") { _, _ ->
                val selectedEmojiIndex = emojiGroup.checkedRadioButtonId
                if (selectedEmojiIndex != -1) {
                    val selectedEmoji = emojiOptions[selectedEmojiIndex].first
                    val note = noteInput.text.toString().trim()
                    
                    viewModel.updateMoodEntry(
                        entry = moodEntry,
                        moodType = emojiOptions[selectedEmojiIndex].second,
                        moodEmoji = selectedEmoji,
                        note = note
                    )
                    
                    // Show confirmation
                    Snackbar.make(
                        binding.root,
                        "Mood updated: ${emojiOptions[selectedEmojiIndex].second}",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
