package com.example.myapp1

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class SharedPreferencesManager(private val context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    companion object {
        @Volatile
        private var instance: SharedPreferencesManager? = null
        
        fun initialize(context: Context) {
            instance ?: synchronized(this) {
                instance ?: SharedPreferencesManager(context.applicationContext).also { instance = it }
            }
        }
        
        fun getInstance(): SharedPreferencesManager {
            return instance ?: throw IllegalStateException(
                "SharedPreferencesManager not initialized. Call initialize() first."
            )
        }
    }

    // Habit Methods
    fun saveHabits(habits: List<Habit>) {
        val json = gson.toJson(habits)
        sharedPref.edit().putString("habits", json).apply()
    }

    fun getHabits(): List<Habit> {
        val json = sharedPref.getString("habits", null)
        return if (json != null) {
            val type = object : TypeToken<List<Habit>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Mood Methods
    fun saveMoodEntries(moodEntries: List<MoodEntry>) {
        val json = gson.toJson(moodEntries)
        sharedPref.edit().putString("mood_entries", json).apply()
    }

    fun getMoodEntries(): List<MoodEntry> {
        val json = sharedPref.getString("mood_entries", null)
        return if (json != null) {
            val type = object : TypeToken<List<MoodEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Settings Methods
    // Hydration Reminder Settings
    fun setHydrationReminderEnabled(enabled: Boolean) {
        sharedPref.edit().putBoolean("hydration_reminder", enabled).apply()
    }
    
    fun isHydrationReminderEnabled(): Boolean {
        return sharedPref.getBoolean("hydration_reminder", true) // Default to true
    }
    
    fun setHydrationReminderInterval(minutes: Int) {
        sharedPref.edit().putInt("hydration_interval", minutes).apply()
    }
    
    fun getHydrationReminderInterval(): Int {
        return sharedPref.getInt("hydration_interval", 1) // Default to 1 minute for demo
    }
    
    // Water Tracking Methods
    fun saveWaterEntries(waterEntries: List<WaterEntry>) {
        val json = gson.toJson(waterEntries)
        sharedPref.edit().putString("water_entries", json).apply()
    }

    fun getWaterEntries(): List<WaterEntry> {
        val json = sharedPref.getString("water_entries", null)
        return if (json != null) {
            val type = object : TypeToken<List<WaterEntry>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }

    // Step Counter Methods
    fun saveStepData(stepData: List<StepData>) {
        val json = gson.toJson(stepData)
        sharedPref.edit().putString("step_data", json).apply()
    }

    fun getStepData(): List<StepData> {
        val json = sharedPref.getString("step_data", null)
        return if (json != null) {
            val type = object : TypeToken<List<StepData>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } else {
            emptyList()
        }
    }
    
    fun setDailyStepGoal(goal: Int) {
        sharedPref.edit().putInt("daily_step_goal", goal).apply()
    }
    
    fun getDailyStepGoal(): Int {
        return sharedPref.getInt("daily_step_goal", 10000) // Default to 10,000 steps
    }

    // Dark Mode Settings - Deprecated, use ThemeManager instead
    @Deprecated("Use ThemeManager.setTheme() instead")
    fun setDarkModeEnabled(enabled: Boolean) {
        sharedPref.edit().putBoolean("dark_mode", enabled).apply()
    }
    
    @Deprecated("Use ThemeManager.getCurrentTheme() instead")
    fun isDarkModeEnabled(): Boolean {
        return sharedPref.getBoolean("dark_mode", false) // Default to light mode
    }
}