package com.example.myapp1

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

class WellnessApp : Application() {
    
    override fun onCreate() {
        super.onCreate()
        // Assign singleton instance
        instance = this
        
        // Create notification channel for hydration reminders
        createNotificationChannel()
        
        // Initialize SharedPreferencesManager with application context
        SharedPreferencesManager.initialize(applicationContext)
    }
    
    companion object {
        // Singleton instance
        private var instance: WellnessApp? = null
        
        fun getInstance(): WellnessApp {
            return instance ?: throw IllegalStateException("Application not initialized")
        }
        
        fun isOnboardingCompleted(context: Context): Boolean {
            val sharedPrefs = context.getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
            return sharedPrefs.getBoolean("onboarding_completed", false)
        }
    }
    
    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Hydration Reminders"
            val descriptionText = "Notifications for hydration reminders"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(
                NotificationHelper.CHANNEL_ID,
                name,
                importance
            ).apply {
                description = descriptionText
            }
            
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}
