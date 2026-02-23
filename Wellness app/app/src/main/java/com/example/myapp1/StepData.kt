package com.example.myapp1

import java.util.Date

data class StepData(
    val id: Long = 0L,
    val steps: Int,
    val date: Date = Date(),
    val goal: Int = 10000 // Default daily step goal
) {
    val progressPercentage: Float
        get() = if (goal > 0) (steps.toFloat() / goal * 100).coerceAtMost(100f) else 0f
    
    val isGoalAchieved: Boolean
        get() = steps >= goal
}
