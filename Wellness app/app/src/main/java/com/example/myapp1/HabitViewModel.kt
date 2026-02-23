package com.example.myapp1

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.*

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    
    private val _habits = MutableStateFlow<List<Habit>>(emptyList())
    val habits: StateFlow<List<Habit>> = _habits.asStateFlow()
    
    private val _selectedHabit = MutableLiveData<Habit?>(null)
    val selectedHabit: LiveData<Habit?> = _selectedHabit
    
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    
    init {
        loadHabits()
    }
    
    fun loadHabits() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val habitsList = sharedPreferencesManager.getHabits()
                _habits.value = habitsList.sortedByDescending { it.date }
            } catch (e: Exception) {
                _error.postValue("Failed to load habits: ${e.message}")
            }
        }
    }
    
    suspend fun getHabitById(id: Long): Habit? {
        return _habits.value.find { it.id == id }
    }
    
    fun saveHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentHabits = _habits.value.toMutableList()
                val existingIndex = currentHabits.indexOfFirst { it.id == habit.id }
                
                if (existingIndex != -1) {
                    // Update existing habit
                    currentHabits[existingIndex] = habit
                } else {
                    // Add new habit with a new ID
                    val newHabit = habit.copy(id = System.currentTimeMillis())
                    currentHabits.add(0, newHabit)
                }
                
                sharedPreferencesManager.saveHabits(currentHabits)
                _habits.value = currentHabits.sortedByDescending { it.date }
            } catch (e: Exception) {
                _error.postValue("Failed to save habit: ${e.message}")
            }
        }
    }
    
    fun deleteHabit(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentHabits = _habits.value.toMutableList()
                currentHabits.removeIf { it.id == habit.id }
                sharedPreferencesManager.saveHabits(currentHabits)
                _habits.value = currentHabits.sortedByDescending { it.date }
            } catch (e: Exception) {
                _error.postValue("Failed to delete habit: ${e.message}")
            }
        }
    }
    
    fun toggleHabitCompletion(habit: Habit) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedHabit = if (habit.isCompleted) {
                    // If already completed, reset for next day/cycle
                    habit.copy(
                        completedCount = 0,
                        isCompleted = false,
                        date = Date()
                    )
                } else {
                    // If not completed, increment count and check if target is reached
                    val newCount = habit.completedCount + 1
                    habit.copy(
                        completedCount = newCount,
                        isCompleted = newCount >= habit.targetCount,
                        date = Date()
                    )
                }
                
                saveHabit(updatedHabit)
            } catch (e: Exception) {
                _error.postValue("Failed to update habit: ${e.message}")
            }
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
