package com.example.myapp1

import android.content.Context
import android.util.Log
import androidx.work.*
import androidx.work.Data
import java.util.concurrent.TimeUnit

object HydrationReminderManager {
    private const val WORKER_TAG = "hydration_reminder_worker"
    private const val UNIQUE_WORK_NAME = "hydration_reminder"
    
    fun scheduleReminder(context: Context, intervalMinutes: Long) {
        try {
            Log.d("HydrationReminderManager", "Scheduling reminder with interval: $intervalMinutes minutes")
            
            // Cancel any existing reminders
            cancelReminder(context)
            
            // Validate interval (minimum 1 minute for demo purposes)
            val validInterval = if (intervalMinutes < 1) {
                Log.w("HydrationReminderManager", "Interval too small ($intervalMinutes), using minimum 1 minute")
                1L
            } else {
                intervalMinutes
            }
            
            // Create a periodic work request
            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED)
                .setRequiresBatteryNotLow(false) // Allow even when battery is low
                .setRequiresCharging(false)
                .setRequiresDeviceIdle(false)
                .build()
            
            // For demo purposes, use different strategies based on interval
            if (validInterval <= 5) {
                // For very short intervals (1-5 minutes), use SuperSimpleNotificationManager
                Log.d("HydrationReminderManager", "Using SuperSimpleNotificationManager for very short interval: $validInterval minutes")
                SuperSimpleNotificationManager.startSimpleReminder(context, validInterval)
            } else if (validInterval <= 10) {
                // For short intervals (6-10 minutes), use InstantNotificationManager
                Log.d("HydrationReminderManager", "Using InstantNotificationManager for short interval: $validInterval minutes")
                InstantNotificationManager.startInstantReminders(context, validInterval)
            } else if (validInterval < 15) {
                // For short intervals (11-14 minutes), use OneTimeWorkRequest with chaining
                scheduleOneTimeReminder(context, validInterval, constraints)
            } else {
                // For longer intervals, use PeriodicWorkRequest
                val flexInterval = (validInterval / 4).coerceAtLeast(5L)
                
                val workRequest = PeriodicWorkRequestBuilder<HydrationReminderWorker>(
                    validInterval, TimeUnit.MINUTES,
                    flexInterval, TimeUnit.MINUTES
                )
                    .setConstraints(constraints)
                    .addTag(WORKER_TAG)
                    .setInitialDelay(validInterval, TimeUnit.MINUTES)
                    .build()
                
                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    "hydration_reminder",
                    ExistingPeriodicWorkPolicy.REPLACE,
                    workRequest
                )
            }
            
            Log.d("HydrationReminderManager", "Reminder scheduled successfully with $validInterval minutes interval")
            
        } catch (e: Exception) {
            Log.e("HydrationReminderManager", "Error scheduling reminder", e)
        }
    }
    
    private fun scheduleOneTimeReminder(context: Context, intervalMinutes: Long, constraints: Constraints) {
        val workRequest = OneTimeWorkRequestBuilder<HydrationReminderWorker>()
            .setConstraints(constraints)
            .addTag(WORKER_TAG)
            .setInitialDelay(intervalMinutes, TimeUnit.MINUTES)
            .setInputData(
                workDataOf(
                    "interval_minutes" to intervalMinutes,
                    "is_recurring" to true
                )
            )
            .build()
        
        WorkManager.getInstance(context).enqueueUniqueWork(
            "hydration_reminder_onetime",
            ExistingWorkPolicy.REPLACE,
            workRequest
        )
    }
    
    fun cancelReminder(context: Context) {
        try {
            Log.d("HydrationReminderManager", "Cancelling hydration reminders")
            
            // Cancel SuperSimpleNotificationManager reminders
            SuperSimpleNotificationManager.stopSimpleReminder()
            
            // Cancel InstantNotificationManager reminders
            InstantNotificationManager.stopInstantReminders(context)
            
            // Cancel ForegroundService reminders
            HydrationForegroundService.stopReminderService(context)
            
            // Cancel AlarmManager reminders
            AlarmReminderManager.cancelAlarmReminder(context)
            
            // Cancel WorkManager reminders
            val workManager = WorkManager.getInstance(context)
            
            // Cancel by tag
            workManager.cancelAllWorkByTag(WORKER_TAG)
            
            // Also cancel by unique work names
            workManager.cancelUniqueWork("hydration_reminder")
            workManager.cancelUniqueWork("hydration_reminder_onetime")
            
            Log.d("HydrationReminderManager", "All reminders cancelled successfully")
            
        } catch (e: Exception) {
            Log.e("HydrationReminderManager", "Error cancelling reminders", e)
        }
    }
    
    fun isReminderScheduled(context: Context): Boolean {
        return try {
            val workInfos = WorkManager.getInstance(context)
                .getWorkInfosByTag(WORKER_TAG)
                .get()
            
            workInfos.any { workInfo ->
                workInfo.state == WorkInfo.State.ENQUEUED || workInfo.state == WorkInfo.State.RUNNING
            }
        } catch (e: Exception) {
            Log.e("HydrationReminderManager", "Error checking reminder status", e)
            false
        }
    }
}

class HydrationReminderWorker(
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {
    
    override fun doWork(): Result {
        return try {
            Log.d("HydrationReminderWorker", "Executing hydration reminder work")
            
            // Check if reminders are still enabled
            val sharedPrefs = applicationContext.getSharedPreferences("WellnessApp", Context.MODE_PRIVATE)
            val isEnabled = sharedPrefs.getBoolean("hydration_reminder", false)
            
            if (!isEnabled) {
                Log.d("HydrationReminderWorker", "Reminders disabled, skipping notification")
                return Result.success()
            }
            
            // Show notification
            NotificationHelper.showHydrationReminder(applicationContext)
            
            // Check if this is a recurring one-time reminder
            val isRecurring = inputData.getBoolean("is_recurring", false)
            val intervalMinutes = inputData.getLong("interval_minutes", 0L)
            
            if (isRecurring && intervalMinutes > 0) {
                // Schedule the next reminder
                Log.d("HydrationReminderWorker", "Scheduling next recurring reminder in $intervalMinutes minutes")
                HydrationReminderManager.scheduleReminder(applicationContext, intervalMinutes)
            }
            
            Log.d("HydrationReminderWorker", "Hydration reminder notification sent successfully")
            
            Result.success()
            
        } catch (e: Exception) {
            Log.e("HydrationReminderWorker", "Error in hydration reminder work", e)
            Result.failure()
        }
    }
}
