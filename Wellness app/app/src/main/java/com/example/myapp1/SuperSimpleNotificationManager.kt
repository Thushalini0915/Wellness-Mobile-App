package com.example.myapp1

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import kotlinx.coroutines.*

object SuperSimpleNotificationManager {
    
    private var notificationJob: Job? = null
    private var isRunning = false
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())
    private val handler = Handler(Looper.getMainLooper())
    private var handlerRunnable: Runnable? = null
    
    fun startSimpleReminder(context: Context, intervalMinutes: Long) {
        Log.d("SuperSimpleNotificationManager", "🚀 STARTING ULTRA AGGRESSIVE REMINDER - $intervalMinutes minutes")
        
        // Stop any existing reminder
        stopSimpleReminder()
        
        isRunning = true
        
        // Method 1: Use Handler for immediate reliability
        startHandlerMethod(context, intervalMinutes)
        
        // Method 2: Use coroutines as backup
        startCoroutineMethod(context, intervalMinutes)
        
        Log.d("SuperSimpleNotificationManager", "✅ DUAL METHOD REMINDER STARTED!")
    }
    
    private fun startHandlerMethod(context: Context, intervalMinutes: Long) {
        handlerRunnable = object : Runnable {
            override fun run() {
                if (isRunning) {
                    try {
                        Log.d("SuperSimpleNotificationManager", "📱 HANDLER: Showing notification NOW!")
                        
                        // Force show notification immediately
                        NotificationHelper.showHydrationReminder(context)
                        
                        // Schedule next one
                        handler.postDelayed(this, intervalMinutes * 60 * 1000)
                        
                        Log.d("SuperSimpleNotificationManager", "✅ HANDLER: Notification sent and next scheduled!")
                        
                    } catch (e: Exception) {
                        Log.e("SuperSimpleNotificationManager", "Handler error", e)
                        // Try again in 5 seconds
                        handler.postDelayed(this, 5000)
                    }
                }
            }
        }
        
        // Start immediately, then repeat
        handler.post(handlerRunnable!!)
        Log.d("SuperSimpleNotificationManager", "⏰ Handler method activated!")
    }
    
    private fun startCoroutineMethod(context: Context, intervalMinutes: Long) {
        notificationJob = scope.launch {
            delay(2000) // Start 2 seconds after handler
            
            while (isRunning) {
                try {
                    delay(intervalMinutes * 60 * 1000)
                    
                    if (isRunning) {
                        Log.d("SuperSimpleNotificationManager", "🔄 COROUTINE: Backup notification")
                        
                        withContext(Dispatchers.Main) {
                            NotificationHelper.showHydrationReminder(context)
                        }
                        
                        Log.d("SuperSimpleNotificationManager", "✅ COROUTINE: Backup sent!")
                    }
                } catch (e: Exception) {
                    Log.e("SuperSimpleNotificationManager", "Coroutine error", e)
                    delay(5000) // Wait 5 seconds before retry
                }
            }
        }
        
        Log.d("SuperSimpleNotificationManager", "🔄 Coroutine backup method activated!")
    }
    
    fun stopSimpleReminder() {
        Log.d("SuperSimpleNotificationManager", "🛑 Stopping all reminder methods")
        
        isRunning = false
        
        // Stop handler
        handlerRunnable?.let { handler.removeCallbacks(it) }
        handlerRunnable = null
        
        // Stop coroutine
        notificationJob?.cancel()
        notificationJob = null
    }
    
    fun isActive(): Boolean = isRunning
}
