package com.example.myapp1

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object InstantNotificationManager {
    
    private val handler = Handler(Looper.getMainLooper())
    private var currentRunnable: Runnable? = null
    private var isActive = false
    private var executor: ScheduledExecutorService? = null
    private const val ALARM_REQUEST_CODE = 9999
    
    fun startInstantReminders(context: Context, intervalMinutes: Long) {
        Log.d("InstantNotificationManager", "🚀 STARTING AGGRESSIVE REMINDER SYSTEM - $intervalMinutes minutes")
        
        // Stop any existing reminders
        stopInstantReminders()
        
        isActive = true
        
        // Method 1: Handler (for when app is active)
        scheduleHandlerReminder(context, intervalMinutes)
        
        // Method 2: AlarmManager (for when app is in background)
        scheduleAlarmReminder(context, intervalMinutes)
        
        // Method 3: ScheduledExecutor (additional backup)
        scheduleExecutorReminder(context, intervalMinutes)
        
        Log.d("InstantNotificationManager", "✅ ALL THREE REMINDER METHODS ACTIVATED!")
    }
    
    fun stopInstantReminders(context: Context? = null) {
        Log.d("InstantNotificationManager", "🛑 Stopping all instant reminders")
        
        isActive = false
        
        // Stop Handler
        currentRunnable?.let { handler.removeCallbacks(it) }
        currentRunnable = null
        
        // Stop Executor
        executor?.shutdown()
        executor = null
        
        // Stop AlarmManager
        context?.let { ctx ->
            try {
                val alarmManager = ctx.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                val intent = Intent(ctx, InstantAlarmReceiver::class.java)
                val pendingIntent = PendingIntent.getBroadcast(
                    ctx,
                    ALARM_REQUEST_CODE,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )
                alarmManager.cancel(pendingIntent)
                pendingIntent.cancel()
                Log.d("InstantNotificationManager", "⏰ AlarmManager cancelled")
            } catch (e: Exception) {
                Log.e("InstantNotificationManager", "Error cancelling alarm", e)
            }
        }
    }
    
    private fun scheduleHandlerReminder(context: Context, intervalMinutes: Long) {
        currentRunnable = Runnable {
            if (isActive) {
                Log.d("InstantNotificationManager", "📱 HANDLER: Showing notification")
                showNotificationAndReschedule(context, intervalMinutes, "HANDLER")
                scheduleHandlerReminder(context, intervalMinutes)
            }
        }
        
        val delayMillis = intervalMinutes * 60 * 1000
        handler.postDelayed(currentRunnable!!, delayMillis)
        Log.d("InstantNotificationManager", "⏰ Handler scheduled for $intervalMinutes minutes")
    }
    
    private fun scheduleAlarmReminder(context: Context, intervalMinutes: Long) {
        try {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, InstantAlarmReceiver::class.java).apply {
                putExtra("interval_minutes", intervalMinutes)
            }
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            val triggerTime = System.currentTimeMillis() + (intervalMinutes * 60 * 1000)
            
            // Use most aggressive alarm method
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            } else {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent)
            }
            
            Log.d("InstantNotificationManager", "⏰ AlarmManager scheduled for $intervalMinutes minutes")
            
        } catch (e: Exception) {
            Log.e("InstantNotificationManager", "Error scheduling alarm", e)
        }
    }
    
    private fun scheduleExecutorReminder(context: Context, intervalMinutes: Long) {
        executor = Executors.newSingleThreadScheduledExecutor()
        
        executor?.scheduleAtFixedRate({
            if (isActive) {
                Log.d("InstantNotificationManager", "⚡ EXECUTOR: Showing notification")
                handler.post {
                    showNotificationAndReschedule(context, intervalMinutes, "EXECUTOR")
                }
            }
        }, intervalMinutes, intervalMinutes, TimeUnit.MINUTES)
        
        Log.d("InstantNotificationManager", "⚡ Executor scheduled for $intervalMinutes minutes")
    }
    
    private fun showNotificationAndReschedule(context: Context, intervalMinutes: Long, source: String) {
        try {
            // Check if reminders are still enabled
            val sharedPrefs = context.getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
            val isEnabled = sharedPrefs.getBoolean("hydration_reminder", false)
            
            if (isEnabled) {
                Log.d("InstantNotificationManager", "🔔 SHOWING NOTIFICATION FROM: $source")
                NotificationHelper.showHydrationReminder(context)
                
                // Reschedule alarm for next time
                if (source == "ALARM") {
                    scheduleAlarmReminder(context, intervalMinutes)
                }
            } else {
                Log.d("InstantNotificationManager", "❌ Reminders disabled, stopping")
                stopInstantReminders()
            }
        } catch (e: Exception) {
            Log.e("InstantNotificationManager", "Error showing notification from $source", e)
        }
    }
    
    fun isActive(): Boolean = isActive
}

class InstantAlarmReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Log.d("InstantAlarmReceiver", "🚨 ALARM TRIGGERED!")
        
        val intervalMinutes = intent.getLongExtra("interval_minutes", 1)
        
        // Show notification immediately
        try {
            val sharedPrefs = context.getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
            val isEnabled = sharedPrefs.getBoolean("hydration_reminder", false)
            
            if (isEnabled) {
                Log.d("InstantAlarmReceiver", "🔔 ALARM: Showing hydration notification")
                NotificationHelper.showHydrationReminder(context)
                
                // Schedule next alarm
                if (InstantNotificationManager.isActive()) {
                    val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
                    val nextIntent = Intent(context, InstantAlarmReceiver::class.java).apply {
                        putExtra("interval_minutes", intervalMinutes)
                    }
                    
                    val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        9999,
                        nextIntent,
                        PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                    )
                    
                    val nextTriggerTime = System.currentTimeMillis() + (intervalMinutes * 60 * 1000)
                    
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextTriggerTime, pendingIntent)
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextTriggerTime, pendingIntent)
                    }
                    
                    Log.d("InstantAlarmReceiver", "⏰ Next alarm scheduled in $intervalMinutes minutes")
                }
            }
        } catch (e: Exception) {
            Log.e("InstantAlarmReceiver", "Error in alarm receiver", e)
        }
    }
}
