package com.example.myapp1

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ArrayAdapter
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.example.myapp1.databinding.DialogAddEditHabitBinding
import kotlinx.coroutines.launch

class AddEditHabitDialog : DialogFragment() {
    private var _binding: DialogAddEditHabitBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HabitViewModel by activityViewModels()
    private var habitId: Long = -1
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            habitId = it.getLong(ARG_HABIT_ID, -1)
        }
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogAddEditHabitBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupFrequencySpinner()
        
        if (habitId != -1L) {
            // Editing existing habit
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.getHabitById(habitId)?.let { habit ->
                    binding.apply {
                        etHabitName.setText(habit.name)
                        etTargetCount.setText(habit.targetCount.toString())
                        // Set the selected frequency in spinner
                        val frequencies = arrayOf("Daily", "Weekly", "Monthly")
                        val frequencyPosition = frequencies.indexOf(habit.frequency)
                        if (frequencyPosition >= 0) {
                            spinnerFrequency.setSelection(frequencyPosition)
                        }
                    }
                }
            }
        }
        
        binding.btnSave.setOnClickListener {
            saveHabit()
        }
        
        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
    
    private fun setupFrequencySpinner() {
        val frequencies = arrayOf("Daily", "Weekly", "Monthly")
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            frequencies
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerFrequency.setAdapter(adapter)
    }
    
    private fun saveHabit() {
        val name = binding.etHabitName.text.toString().trim()
        val targetCount = binding.etTargetCount.text.toString().toIntOrNull() ?: 1
        val frequency = binding.spinnerFrequency.text.toString().ifBlank { "Daily" }
        
        if (name.isBlank()) {
            binding.etHabitName.error = "Habit name cannot be empty"
            return
        }
        
        if (targetCount <= 0) {
            binding.etTargetCount.error = "Target count must be greater than 0"
            return
        }
        
        viewLifecycleOwner.lifecycleScope.launch {
            val habit = if (habitId != -1L) {
                // Editing existing habit - preserve current values
                val existingHabit = viewModel.getHabitById(habitId)
                existingHabit?.copy(
                    name = name,
                    targetCount = targetCount,
                    frequency = frequency,
                    date = java.util.Date()
                ) ?: Habit(
                    id = habitId,
                    name = name,
                    targetCount = targetCount,
                    completedCount = 0,
                    frequency = frequency,
                    date = java.util.Date(),
                    isCompleted = false
                )
            } else {
                // Creating new habit
                Habit(
                    id = 0,
                    name = name,
                    targetCount = targetCount,
                    completedCount = 0,
                    frequency = frequency,
                    date = java.util.Date(),
                    isCompleted = false
                )
            }
            
            viewModel.saveHabit(habit)
            dismiss()
        }
    }
    
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        // Request focus for the dialog
        dialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        return dialog
    }
    
    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    
    companion object {
        private const val ARG_HABIT_ID = "habit_id"
        
        fun newInstance(habitId: Long = -1): AddEditHabitDialog {
            return AddEditHabitDialog().apply {
                arguments = Bundle().apply {
                    putLong(ARG_HABIT_ID, habitId)
                }
            }
        }
    }
}
