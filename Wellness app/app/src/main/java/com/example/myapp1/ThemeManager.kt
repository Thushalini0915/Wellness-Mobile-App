package com.example.myapp1

import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatDelegate


object ThemeManager {
    
    private const val PREF_NAME = "theme_preferences"
    private const val KEY_THEME_MODE = "theme_mode"
    
    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"
    const val THEME_SYSTEM = "system"
    

    fun applyTheme(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val themePreference = sharedPrefs.getString(KEY_THEME_MODE, THEME_SYSTEM) ?: THEME_SYSTEM
        
        when (themePreference) {
            THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            THEME_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
    

    fun applyTheme(context: Context, themeMode: String) {
        // Check if theme is already set to avoid unnecessary work
        val currentTheme = getCurrentTheme(context)
        if (currentTheme == themeMode) {
            return // Theme is already set, no need to change
        }
        
        val mode = when (themeMode) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            THEME_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
        
        AppCompatDelegate.setDefaultNightMode(mode)
        
        // Save preference
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME_MODE, themeMode)
            .apply()
    }
    

    fun applyThemeAndRecreate(activity: android.app.Activity, themeMode: String) {
        applyTheme(activity, themeMode)
        activity.recreate()
    }
    

    fun initializeTheme(context: Context) {
        val savedTheme = getCurrentTheme(context)
        
        // If no theme is saved, set light as default
        if (savedTheme == THEME_LIGHT) {
            // Ensure light theme is saved as preference
            context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
                .edit()
                .putString(KEY_THEME_MODE, THEME_LIGHT)
                .apply()
        }
        
        applyTheme(context, savedTheme)
    }
    

    fun isDarkTheme(context: Context): Boolean {
        val currentTheme = getCurrentTheme(context)
        return when (currentTheme) {
            THEME_DARK -> true
            THEME_LIGHT -> false
            THEME_SYSTEM -> {
                val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                nightModeFlags == Configuration.UI_MODE_NIGHT_YES
            }
            else -> false
        }
    }
    

    fun setTheme(context: Context, theme: String) {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit().putString(KEY_THEME_MODE, theme).apply()
        
        when (theme) {
            THEME_LIGHT -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            }
            THEME_DARK -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            }
            THEME_SYSTEM -> {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            }
        }
    }
    

    fun getCurrentTheme(context: Context): String {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return sharedPrefs.getString(KEY_THEME_MODE, THEME_LIGHT) ?: THEME_LIGHT
    }
    

    fun isDarkMode(context: Context): Boolean {
        val currentTheme = getCurrentTheme(context)
        return when (currentTheme) {
            THEME_DARK -> true
            THEME_LIGHT -> false
            THEME_SYSTEM -> {
                val nightModeFlags = context.resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK
                nightModeFlags == Configuration.UI_MODE_NIGHT_YES
            }
            else -> false
        }
    }
    

    fun getThemeMode(context: Context): Int {
        val themePreference = getCurrentTheme(context)
        return when (themePreference) {
            THEME_LIGHT -> AppCompatDelegate.MODE_NIGHT_NO
            THEME_DARK -> AppCompatDelegate.MODE_NIGHT_YES
            THEME_SYSTEM -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }
    

    fun toggleTheme(context: Context) {
        val currentTheme = getCurrentTheme(context)
        val newTheme = if (currentTheme == THEME_DARK) THEME_LIGHT else THEME_DARK
        setTheme(context, newTheme)
    }
}
