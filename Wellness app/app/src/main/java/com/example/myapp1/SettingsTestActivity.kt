package com.example.myapp1

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.myapp1.databinding.ActivitySettingsTestBinding
import kotlinx.coroutines.launch

class SettingsTestActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivitySettingsTestBinding
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsTestBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Initialize SharedPreferencesManager
        try {
            sharedPreferencesManager = SharedPreferencesManager.getInstance()
        } catch (e: IllegalStateException) {
            SharedPreferencesManager.initialize(applicationContext)
            sharedPreferencesManager = SharedPreferencesManager.getInstance()
        }
        
        setupTestButtons()
        loadCurrentSettings()
    }
    
    private fun setupTestButtons() {
        binding.btnTestReminder.setOnClickListener {
            testReminderFunctionality()
        }
        
        binding.btnTestLogout.setOnClickListener {
            testLogoutFunctionality()
        }
        
        binding.btnTestNotification.setOnClickListener {
            testNotificationFunctionality()
        }
        
        binding.btnRefreshSettings.setOnClickListener {
            loadCurrentSettings()
        }
    }
    
    private fun testReminderFunctionality() {
        lifecycleScope.launch {
            try {
                Log.d("SettingsTest", "Testing reminder functionality...")
                
                // Test enabling reminder
                sharedPreferencesManager.setHydrationReminderEnabled(true)
                sharedPreferencesManager.setHydrationReminderInterval(30)
                
                // Schedule reminder
                HydrationReminderManager.scheduleReminder(this@SettingsTestActivity, 30)
                
                // Check if scheduled
                val isScheduled = HydrationReminderManager.isReminderScheduled(this@SettingsTestActivity)
                
                binding.tvTestResults.text = buildString {
                    append("Reminder Test Results:\n")
                    append("Enabled: ${sharedPreferencesManager.isHydrationReminderEnabled()}\n")
                    append("Interval: ${sharedPreferencesManager.getHydrationReminderInterval()} min\n")
                    append("Scheduled: $isScheduled\n")
                    append("Test completed at: ${System.currentTimeMillis()}\n")
                }
                
                Log.d("SettingsTest", "Reminder test completed successfully")
                
            } catch (e: Exception) {
                Log.e("SettingsTest", "Error testing reminder functionality", e)
                binding.tvTestResults.text = "Reminder test failed: ${e.message}"
            }
        }
    }
    
    private fun testLogoutFunctionality() {
        try {
            Log.d("SettingsTest", "Testing logout functionality...")
            
            // Check current login status
            val wasLoggedIn = AuthManager.isLoggedIn(this)
            
            // If not logged in, simulate login first
            if (!wasLoggedIn) {
                AuthManager.saveUserLogin(this, "test@example.com", "Test User")
            }
            
            val isLoggedInAfterLogin = AuthManager.isLoggedIn(this)
            val userName = AuthManager.getCurrentUserName(this)
            val userEmail = AuthManager.getCurrentUserEmail(this)
            
            // Test logout
            AuthManager.logout(this)
            val isLoggedInAfterLogout = AuthManager.isLoggedIn(this)
            
            binding.tvTestResults.text = buildString {
                append("Logout Test Results:\n")
                append("Was logged in initially: $wasLoggedIn\n")
                append("Logged in after login: $isLoggedInAfterLogin\n")
                append("User name: $userName\n")
                append("User email: $userEmail\n")
                append("Logged in after logout: $isLoggedInAfterLogout\n")
                append("Test completed at: ${System.currentTimeMillis()}\n")
            }
            
            Log.d("SettingsTest", "Logout test completed successfully")
            
        } catch (e: Exception) {
            Log.e("SettingsTest", "Error testing logout functionality", e)
            binding.tvTestResults.text = "Logout test failed: ${e.message}"
        }
    }
    
    private fun testNotificationFunctionality() {
        try {
            Log.d("SettingsTest", "Testing notification functionality...")
            
            // Create notification channel
            NotificationHelper.createNotificationChannel(this)
            
            // Check if notifications are enabled
            val areNotificationsEnabled = NotificationHelper.areNotificationsEnabled(this)
            
            // Show test notification
            NotificationHelper.showHydrationReminder(this)
            
            binding.tvTestResults.text = buildString {
                append("Notification Test Results:\n")
                append("Notifications enabled: $areNotificationsEnabled\n")
                append("Test notification sent\n")
                append("Check your notification panel\n")
                append("Test completed at: ${System.currentTimeMillis()}\n")
            }
            
            Log.d("SettingsTest", "Notification test completed successfully")
            
        } catch (e: Exception) {
            Log.e("SettingsTest", "Error testing notification functionality", e)
            binding.tvTestResults.text = "Notification test failed: ${e.message}"
        }
    }
    
    private fun loadCurrentSettings() {
        try {
            val isReminderEnabled = sharedPreferencesManager.isHydrationReminderEnabled()
            val reminderInterval = sharedPreferencesManager.getHydrationReminderInterval()
            val isLoggedIn = AuthManager.isLoggedIn(this)
            val userName = AuthManager.getCurrentUserName(this)
            val userEmail = AuthManager.getCurrentUserEmail(this)
            val currentTheme = ThemeManager.getCurrentTheme(this)
            val isReminderScheduled = HydrationReminderManager.isReminderScheduled(this)
            
            binding.tvCurrentSettings.text = buildString {
                append("Current Settings:\n")
                append("Reminder enabled: $isReminderEnabled\n")
                append("Reminder interval: $reminderInterval min\n")
                append("Reminder scheduled: $isReminderScheduled\n")
                append("User logged in: $isLoggedIn\n")
                append("User name: $userName\n")
                append("User email: $userEmail\n")
                append("Current theme: $currentTheme\n")
                append("Last updated: ${System.currentTimeMillis()}\n")
            }
            
        } catch (e: Exception) {
            Log.e("SettingsTest", "Error loading current settings", e)
            binding.tvCurrentSettings.text = "Error loading settings: ${e.message}"
        }
    }
}
