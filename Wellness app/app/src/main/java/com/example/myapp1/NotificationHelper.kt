package com.example.myapp1

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

object NotificationHelper {
    const val CHANNEL_ID = "hydration_reminder_channel"
    private const val NOTIFICATION_ID = 1001
    
    fun createNotificationChannel(context: Context) {
        try {
            // Create the NotificationChannel, but only on API 26+ because
            // the NotificationChannel class is new and not in the support library
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                Log.d("NotificationHelper", "Creating notification channel for API 26+")
                
                val name = "Hydration Reminders"
                val descriptionText = "Get reminded to drink water throughout the day"
                val importance = NotificationManager.IMPORTANCE_HIGH // High importance for demo
                
                val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                    description = descriptionText
                    enableVibration(true)
                    vibrationPattern = longArrayOf(0, 250, 250, 250)
                    setShowBadge(true)
                }
                
                // Register the channel with the system
                val notificationManager: NotificationManager =
                    context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                notificationManager.createNotificationChannel(channel)
                
                Log.d("NotificationHelper", "Notification channel created successfully")
            } else {
                Log.d("NotificationHelper", "API level < 26, notification channel not needed")
            }
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error creating notification channel", e)
        }
    }
    
    fun areNotificationsEnabled(context: Context): Boolean {
        return try {
            val notificationManager = NotificationManagerCompat.from(context)
            notificationManager.areNotificationsEnabled()
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error checking notification permissions", e)
            false
        }
    }
    
    fun showHydrationReminder(context: Context) {
        try {
            Log.d("NotificationHelper", "Attempting to show hydration reminder notification")
            
            // Ensure notification channel is created
            createNotificationChannel(context)
            
            // Check if notifications are enabled
            val notificationManager = NotificationManagerCompat.from(context)
            if (!notificationManager.areNotificationsEnabled()) {
                Log.w("NotificationHelper", "Notifications are disabled for this app")
                return
            }
            
            // Create an explicit intent for an Activity in your app
            val intent = Intent(context, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("from_notification", true)
                putExtra("notification_type", "hydration_reminder")
            }
            
            val pendingIntentFlags = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            } else {
                PendingIntent.FLAG_UPDATE_CURRENT
            }
            
            val pendingIntent: PendingIntent = PendingIntent.getActivity(
                context, NOTIFICATION_ID, intent, pendingIntentFlags
            )
            
            // Get a random motivational message
            val messages = arrayOf(
                "Time to hydrate! 💧",
                "Don't forget to drink water! 🥤",
                "Stay hydrated, stay healthy! 💙",
                "Your body needs water! 💧",
                "Hydration break time! 🌊"
            )
            val randomMessage = messages.random()
            
            val builder = NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("💧 Hydration Reminder")
                .setContentText(randomMessage)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$randomMessage\n\nTap to open your wellness app!"))
                .setPriority(NotificationCompat.PRIORITY_MAX) // Maximum priority for demo
                .setDefaults(NotificationCompat.DEFAULT_ALL) // Sound, vibration, lights
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 500, 250, 500)) // Stronger vibration
                .setLights(0xFF0000FF.toInt(), 1000, 1000) // Blue light
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setOngoing(false)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
            
            notificationManager.notify(NOTIFICATION_ID, builder.build())
            
            Log.d("NotificationHelper", "Hydration reminder notification sent successfully")
            
        } catch (e: SecurityException) {
            Log.e("NotificationHelper", "Security exception when showing notification", e)
        } catch (e: Exception) {
            Log.e("NotificationHelper", "Error showing hydration reminder notification", e)
        }
    }
}
