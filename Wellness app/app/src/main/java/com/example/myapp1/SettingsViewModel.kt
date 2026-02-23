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
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val sharedPreferencesManager = SharedPreferencesManager.getInstance()
    
    private val _isHydrationReminderEnabled = MutableStateFlow(false)
    val isHydrationReminderEnabled: StateFlow<Boolean> = _isHydrationReminderEnabled.asStateFlow()
    
    private val _hydrationReminderInterval = MutableStateFlow(60) // Default 60 minutes
    val hydrationReminderInterval: StateFlow<Int> = _hydrationReminderInterval.asStateFlow()
    
    private val _currentTheme = MutableStateFlow(ThemeManager.THEME_SYSTEM)
    val currentTheme: StateFlow<String> = _currentTheme.asStateFlow()
    
    val isDarkModeEnabled: StateFlow<Boolean> = MutableStateFlow(false).apply {
        // This will be updated when currentTheme changes
    }.asStateFlow()
    
    private val _error = MutableLiveData<String?>(null)
    val error: LiveData<String?> = _error
    
    init {
        loadSettings()
    }
    
    private fun loadSettings() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Load hydration reminder settings
                _isHydrationReminderEnabled.value = sharedPreferencesManager.isHydrationReminderEnabled()
                _hydrationReminderInterval.value = sharedPreferencesManager.getHydrationReminderInterval()
                
                // Load theme preference
                val theme = ThemeManager.getCurrentTheme(getApplication())
                _currentTheme.value = theme
            } catch (e: Exception) {
                _error.postValue("Failed to load settings: ${e.message}")
            }
        }
    }
    
    fun setHydrationReminderEnabled(enabled: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sharedPreferencesManager.setHydrationReminderEnabled(enabled)
                _isHydrationReminderEnabled.value = enabled
                
                if (enabled) {
                    // Start or update the hydration reminder
                    val intervalMinutes = _hydrationReminderInterval.value
                    HydrationReminderManager.scheduleReminder(getApplication(), intervalMinutes.toLong())
                } else {
                    // Cancel the hydration reminder
                    HydrationReminderManager.cancelReminder(getApplication())
                }
            } catch (e: Exception) {
                _error.postValue("Failed to update hydration reminder: ${e.message}")
                // Revert the UI state on error
                _isHydrationReminderEnabled.value = !enabled
            }
        }
    }
    
    fun setHydrationReminderInterval(intervalMinutes: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                sharedPreferencesManager.setHydrationReminderInterval(intervalMinutes)
                _hydrationReminderInterval.value = intervalMinutes
                
                // Update the reminder if it's currently enabled
                if (_isHydrationReminderEnabled.value) {
                    HydrationReminderManager.scheduleReminder(getApplication(), intervalMinutes.toLong())
                }
            } catch (e: Exception) {
                _error.postValue("Failed to update reminder interval: ${e.message}")
            }
        }
    }
    
    fun setTheme(theme: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                ThemeManager.setTheme(getApplication(), theme)
                _currentTheme.value = theme
            } catch (e: Exception) {
                _error.postValue("Failed to update theme: ${e.message}")
            }
        }
    }
    
    fun setDarkModeEnabled(enabled: Boolean) {
        val theme = if (enabled) ThemeManager.THEME_DARK else ThemeManager.THEME_LIGHT
        setTheme(theme)
    }
    
    fun clearError() {
        _error.value = null
    }
    
    fun getAppVersion(): String {
        return try {
            val packageInfo = getApplication<Application>().packageManager
                .getPackageInfo(getApplication<Application>().packageName, 0)
            packageInfo.versionName ?: "1.0.0"
        } catch (e: Exception) {
            "1.0.0" // Default version if package info can't be retrieved
        }
    }
}
