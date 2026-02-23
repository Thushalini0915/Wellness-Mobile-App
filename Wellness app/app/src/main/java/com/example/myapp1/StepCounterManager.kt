package com.example.myapp1

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.util.Log
import androidx.core.app.ActivityCompat
import java.util.*

class StepCounterManager(private val context: Context) : SensorEventListener {
    
    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val stepCounterSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER)
    private val stepDetectorSensor = sensorManager.getDefaultSensor(Sensor.TYPE_STEP_DETECTOR)
    
    private var stepCount = 0
    private var lastStepCount = 0
    private var isListening = false
    
    private val listeners = mutableListOf<StepCountListener>()
    
    interface StepCountListener {
        fun onStepCountChanged(steps: Int)
    }
    
    fun addStepCountListener(listener: StepCountListener) {
        listeners.add(listener)
    }
    
    fun removeStepCountListener(listener: StepCountListener) {
        listeners.remove(listener)
    }
    
    fun startListening() {
        if (!hasPermission()) {
            Log.w(TAG, "Step counter permission not granted")
            return
        }
        
        if (isListening) return
        
        stepCounterSensor?.let { sensor ->
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI)
            isListening = true
            Log.d(TAG, "Started listening to step counter")
        } ?: run {
            Log.w(TAG, "Step counter sensor not available")
        }
    }
    
    fun stopListening() {
        if (!isListening) return
        
        sensorManager.unregisterListener(this)
        isListening = false
        Log.d(TAG, "Stopped listening to step counter")
    }
    
    fun getCurrentStepCount(): Int = stepCount
    
    fun resetStepCount() {
        lastStepCount = stepCount
        stepCount = 0
        notifyListeners()
    }
    
    private fun hasPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            context,
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun notifyListeners() {
        listeners.forEach { it.onStepCountChanged(stepCount) }
    }
    
    override fun onSensorChanged(event: SensorEvent?) {
        event?.let {
            when (it.sensor.type) {
                Sensor.TYPE_STEP_COUNTER -> {
                    val totalSteps = it.values[0].toInt()
                    if (lastStepCount == 0) {
                        lastStepCount = totalSteps
                    }
                    stepCount = totalSteps - lastStepCount
                    notifyListeners()
                    Log.d(TAG, "Step count: $stepCount (Total: $totalSteps)")
                }
                Sensor.TYPE_STEP_DETECTOR -> {
                    // This sensor fires for each individual step
                    stepCount++
                    notifyListeners()
                    Log.d(TAG, "Step detected, count: $stepCount")
                }
            }
        }
    }
    
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        Log.d(TAG, "Sensor accuracy changed: $accuracy")
    }
    
    companion object {
        private const val TAG = "StepCounterManager"
    }
}
