package com.example.myapp1

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat

class HydrationForegroundService : Service() {
    
    private val handler = Handler(Looper.getMainLooper())
    private var reminderRunnable: Runnable? = null
    private var intervalMinutes: Long = 1
    private var isReminderEnabled = false
    
    companion object {
        const val ACTION_START_REMINDER = "START_REMINDER"
        const val ACTION_STOP_REMINDER = "STOP_REMINDER"
        const val EXTRA_INTERVAL_MINUTES = "interval_minutes"
        const val FOREGROUND_SERVICE_ID = 2001
        const val FOREGROUND_CHANNEL_ID = "hydration_foreground_service"
        
        fun startReminderService(context: Context, intervalMinutes: Long) {
            val intent = Intent(context, HydrationForegroundService::class.java).apply {
                action = ACTION_START_REMINDER
                putExtra(EXTRA_INTERVAL_MINUTES, intervalMinutes)
            }
            
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }
        
        fun stopReminderService(context: Context) {
            val intent = Intent(context, HydrationForegroundService::class.java).apply {
                action = ACTION_STOP_REMINDER
            }
            context.startService(intent)
        }
    }
    
    override fun onCreate() {
        super.onCreate()
        Log.d("HydrationForegroundService", "Service created")
        createForegroundNotificationChannel()
    }
    
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d("HydrationForegroundService", "Service started with action: ${intent?.action}")
        
        when (intent?.action) {
            ACTION_START_REMINDER -> {
                intervalMinutes = intent.getLongExtra(EXTRA_INTERVAL_MINUTES, 1)
                startForegroundReminder()
            }
            ACTION_STOP_REMINDER -> {
                stopForegroundReminder()
            }
        }
        
        return START_STICKY // Restart if killed
    }
    
    private fun startForegroundReminder() {
        Log.d("HydrationForegroundService", "Starting foreground reminder with interval: $intervalMinutes minutes")
        
        // Start foreground service
        val foregroundNotification = createForegroundNotification()
        startForeground(FOREGROUND_SERVICE_ID, foregroundNotification)
        
        isReminderEnabled = true
        scheduleNextReminder()
    }
    
    private fun stopForegroundReminder() {
        Log.d("HydrationForegroundService", "Stopping foreground reminder")
        
        isReminderEnabled = false
        reminderRunnable?.let { handler.removeCallbacks(it) }
        stopForeground(true)
        stopSelf()
    }
    
    private fun scheduleNextReminder() {
        if (!isReminderEnabled) return
        
        reminderRunnable?.let { handler.removeCallbacks(it) }
        
        reminderRunnable = Runnable {
            if (isReminderEnabled) {
                Log.d("HydrationForegroundService", "Showing hydration reminder")
                showHydrationNotification()
                scheduleNextReminder() // Schedule next one
            }
        }
        
        val delayMillis = intervalMinutes * 60 * 1000
        handler.postDelayed(reminderRunnable!!, delayMillis)
        
        Log.d("HydrationForegroundService", "Next reminder scheduled in $intervalMinutes minutes")
    }
    
    private fun showHydrationNotification() {
        try {
            // Check if reminders are still enabled in preferences
            val sharedPrefs = getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
            val isEnabled = sharedPrefs.getBoolean("hydration_reminder", false)
            
            if (!isEnabled) {
                Log.d("HydrationForegroundService", "Reminders disabled in preferences, stopping service")
                stopForegroundReminder()
                return
            }
            
            val messages = arrayOf(
                "💧 Time to hydrate!",
                "🥤 Don't forget to drink water!",
                "💙 Stay hydrated, stay healthy!",
                "🌊 Your body needs water!",
                "💦 Hydration break time!",
                "✨ Keep yourself hydrated!"
            )
            
            val randomMessage = messages.random()
            
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                putExtra("from_notification", true)
            }
            
            val pendingIntent = PendingIntent.getActivity(
                this, 
                System.currentTimeMillis().toInt(),
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val notification = NotificationCompat.Builder(this, NotificationHelper.CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("💧 Hydration Reminder")
                .setContentText(randomMessage)
                .setStyle(NotificationCompat.BigTextStyle().bigText("$randomMessage\n\nTap to open your wellness app!"))
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setVibrate(longArrayOf(0, 1000, 500, 1000)) // Strong vibration
                .setCategory(NotificationCompat.CATEGORY_REMINDER)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .setShowWhen(true)
                .setWhen(System.currentTimeMillis())
                .build()
            
            val notificationManager = NotificationManagerCompat.from(this)
            notificationManager.notify(System.currentTimeMillis().toInt(), notification)
            
            Log.d("HydrationForegroundService", "✅ HYDRATION REMINDER SHOWN: $randomMessage")
            
        } catch (e: Exception) {
            Log.e("HydrationForegroundService", "Error showing notification", e)
        }
    }
    
    private fun createForegroundNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                FOREGROUND_CHANNEL_ID,
                "Hydration Service",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Keeps hydration reminders running in background"
                setShowBadge(false)
            }
            
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
    
    private fun createForegroundNotification(): Notification {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        
        return NotificationCompat.Builder(this, FOREGROUND_CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("Hydration Reminders Active")
            .setContentText("Reminders every $intervalMinutes minute(s)")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .setOngoing(true)
            .setShowWhen(false)
            .build()
    }
    
    override fun onBind(intent: Intent?): IBinder? = null
    
    override fun onDestroy() {
        super.onDestroy()
        Log.d("HydrationForegroundService", "Service destroyed")
        reminderRunnable?.let { handler.removeCallbacks(it) }
    }
}
