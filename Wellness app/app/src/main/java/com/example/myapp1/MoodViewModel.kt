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

class MoodViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    
    private val _moodEntries = MutableStateFlow<List<MoodEntry>>(emptyList())
    val moodEntries: StateFlow<List<MoodEntry>> = _moodEntries.asStateFlow()
    
    private val _selectedMood = MutableLiveData<MoodEntry?>(null)
    val selectedMood: LiveData<MoodEntry?> = _selectedMood
    
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    
    // For mood statistics
    private val _moodStats = MutableStateFlow<Map<String, Int>>(emptyMap())
    val moodStats: StateFlow<Map<String, Int>> = _moodStats.asStateFlow()
    
    init {
        loadMoodEntries()
    }
    
    fun loadMoodEntries() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val entries = sharedPreferencesManager.getMoodEntries()
                val sortedEntries = entries.sortedByDescending { it.date }
                _moodEntries.value = sortedEntries
                updateMoodStats(sortedEntries)
            } catch (e: Exception) {
                _error.postValue("Failed to load mood entries: ${e.message}")
            }
        }
    }
    
    private fun updateMoodStats(entries: List<MoodEntry>) {
        val stats = mutableMapOf<String, Int>()
        val moodTypes = getApplication<Application>().resources.getStringArray(R.array.mood_types)
        
        // Initialize all mood types with 0
        moodTypes.forEach { mood ->
            stats[mood] = 0
        }
        
        // Count each mood type
        entries.forEach { entry ->
            val mood = entry.moodType
            stats[mood] = (stats[mood] ?: 0) + 1
        }
        
        _moodStats.value = stats
    }
    
    fun saveMoodEntry(moodType: String, moodEmoji: String, note: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val newEntry = MoodEntry(
                    id = System.currentTimeMillis(),
                    moodType = moodType,
                    moodEmoji = moodEmoji,
                    note = note,
                    date = Date()
                )
                
                val currentEntries = _moodEntries.value.toMutableList()
                currentEntries.add(0, newEntry)
                
                sharedPreferencesManager.saveMoodEntries(currentEntries)
                _moodEntries.value = currentEntries.sortedByDescending { it.date }
            } catch (e: Exception) {
                _error.postValue("Failed to save mood entry: ${e.message}")
            }
        }
    }
    
    fun updateMoodEntry(entry: MoodEntry, moodType: String, moodEmoji: String, note: String = "") {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val updatedEntry = entry.copy(
                    moodType = moodType,
                    moodEmoji = moodEmoji,
                    note = note,
                    date = Date() // Update the date to current time
                )
                
                val currentEntries = _moodEntries.value.toMutableList()
                val index = currentEntries.indexOfFirst { it.id == entry.id }
                if (index != -1) {
                    currentEntries[index] = updatedEntry
                    sharedPreferencesManager.saveMoodEntries(currentEntries)
                    _moodEntries.value = currentEntries.sortedByDescending { it.date }
                }
            } catch (e: Exception) {
                _error.postValue("Failed to update mood entry: ${e.message}")
            }
        }
    }
    
    fun deleteMoodEntry(entry: MoodEntry) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val currentEntries = _moodEntries.value.toMutableList()
                currentEntries.removeIf { it.id == entry.id }
                sharedPreferencesManager.saveMoodEntries(currentEntries)
                _moodEntries.value = currentEntries.sortedByDescending { it.date }
            } catch (e: Exception) {
                _error.postValue("Failed to delete mood entry: ${e.message}")
            }
        }
    }
    
    fun getMoodEntriesForDate(date: Date): List<MoodEntry> {
        val calendar = Calendar.getInstance()
        calendar.time = date
        
        return _moodEntries.value.filter { entry ->
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = entry.date
            
            calendar.get(Calendar.YEAR) == entryCalendar.get(Calendar.YEAR) &&
            calendar.get(Calendar.MONTH) == entryCalendar.get(Calendar.MONTH) &&
            calendar.get(Calendar.DAY_OF_MONTH) == entryCalendar.get(Calendar.DAY_OF_MONTH)
        }
    }
    
    fun getMoodColor(moodType: String): Int {
        return when (moodType.lowercase(Locale.ROOT)) {
            "happy" -> R.color.mood_happy
            "sad" -> R.color.mood_sad
            "angry" -> R.color.mood_angry
            "tired" -> R.color.mood_tired
            "calm" -> R.color.mood_calm
            else -> R.color.primary
        }
    }
    
    fun clearError() {
        _error.value = null
    }
}
