package com.example.myapp1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import com.example.myapp1.databinding.FragmentHabitTrackerBinding
import com.google.android.material.snackbar.Snackbar

class HabitTrackerFragment : Fragment() {

    private var _binding: FragmentHabitTrackerBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: HabitViewModel by activityViewModels()
    private lateinit var habitsAdapter: HabitsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitTrackerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }
    
    private fun setupRecyclerView() {
        habitsAdapter = HabitsAdapter { habit, action ->
            when (action) {
                HabitAction.EDIT -> showEditHabitDialog(habit)
                HabitAction.DELETE -> viewModel.deleteHabit(habit)
                HabitAction.TOGGLE -> viewModel.toggleHabitCompletion(habit)
            }
        }
        
        binding.rvHabits.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = habitsAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupClickListeners() {
        // Ensure FAB is visible and clickable
        binding.fabAddHabit.visibility = View.VISIBLE
        binding.fabAddHabit.setOnClickListener {
            showAddHabitDialog()
        }
        
        // Setup swipe refresh
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadHabits()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
    
    private fun showAddHabitDialog() {
        val dialog = AddEditHabitDialog.newInstance()
        dialog.show(parentFragmentManager, "AddHabitDialog")
    }
    
    private fun showEditHabitDialog(habit: Habit) {
        val dialog = AddEditHabitDialog.newInstance(habit.id)
        dialog.show(parentFragmentManager, "EditHabitDialog")
    }
    
    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.habits.collect { habits ->
                habitsAdapter.submitList(habits)
                
                // Show empty state if no habits
                if (habits.isEmpty()) {
                    binding.emptyState.root.visibility = View.VISIBLE
                    binding.rvHabits.visibility = View.GONE
                    binding.headerCard.visibility = View.GONE
                } else {
                    binding.emptyState.root.visibility = View.GONE
                    binding.rvHabits.visibility = View.VISIBLE
                    binding.headerCard.visibility = View.VISIBLE
                }
            }
        }
        
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            errorMessage?.let {
                showError(it)
                viewModel.clearError()
            }
        }
    }
    
    private fun showError(message: String) {
        view?.let {
            Snackbar.make(it, message, Snackbar.LENGTH_LONG).show()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}