package com.example.myapp1

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import java.util.*

object AlarmReminderManager {
    
    private const val ALARM_REQUEST_CODE = 1001
    
    fun scheduleAlarmReminder(context: Context, intervalMinutes: Long) {
        try {
            Log.d("AlarmReminderManager", "Scheduling alarm reminder with interval: $intervalMinutes minutes")
            
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, HydrationAlarmReceiver::class.java)
            intent.putExtra("interval_minutes", intervalMinutes)
            
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            // Calculate trigger time
            val triggerTime = System.currentTimeMillis() + (intervalMinutes * 60 * 1000)
            
            // Use setExactAndAllowWhileIdle for better reliability on modern Android
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            } else {
                alarmManager.setExact(
                    AlarmManager.RTC_WAKEUP,
                    triggerTime,
                    pendingIntent
                )
            }
            
            Log.d("AlarmReminderManager", "Alarm scheduled for ${Date(triggerTime)}")
            
        } catch (e: Exception) {
            Log.e("AlarmReminderManager", "Error scheduling alarm reminder", e)
        }
    }
    
    fun cancelAlarmReminder(context: Context) {
        try {
            Log.d("AlarmReminderManager", "Cancelling alarm reminder")
            
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, HydrationAlarmReceiver::class.java)
            val pendingIntent = PendingIntent.getBroadcast(
                context,
                ALARM_REQUEST_CODE,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
            
            Log.d("AlarmReminderManager", "Alarm reminder cancelled")
            
        } catch (e: Exception) {
            Log.e("AlarmReminderManager", "Error cancelling alarm reminder", e)
        }
    }
}

class HydrationAlarmReceiver : BroadcastReceiver() {
    
    override fun onReceive(context: Context, intent: Intent) {
        try {
            Log.d("HydrationAlarmReceiver", "Alarm received, showing notification")
            
            // Check if reminders are still enabled
            val sharedPrefs = context.getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
            val isEnabled = sharedPrefs.getBoolean("hydration_reminder", false)
            
            if (!isEnabled) {
                Log.d("HydrationAlarmReceiver", "Reminders disabled, skipping notification")
                return
            }
            
            // Show notification
            NotificationHelper.showHydrationReminder(context)
            
            // Get interval and reschedule if needed
            val intervalMinutes = intent.getLongExtra("interval_minutes", 0L)
            if (intervalMinutes > 0) {
                Log.d("HydrationAlarmReceiver", "Rescheduling next alarm in $intervalMinutes minutes")
                AlarmReminderManager.scheduleAlarmReminder(context, intervalMinutes)
            }
            
        } catch (e: Exception) {
            Log.e("HydrationAlarmReceiver", "Error handling alarm", e)
        }
    }
}
