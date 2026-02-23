package com.example.myapp1

import java.util.Date

data class Habit(
    val id: Long = 0L,
    val name: String,
    val targetCount: Int = 1,
    val completedCount: Int = 0,
    val frequency: String = "Daily",
    val date: Date = Date(),
    val isCompleted: Boolean = false
) {
    fun getCompletionPercentage(): Float {
        return if (targetCount > 0) {
            (completedCount.toFloat() / targetCount.toFloat()) * 100
        } else 0f
    }
}