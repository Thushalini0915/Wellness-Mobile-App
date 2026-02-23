package com.example.myapp1

import java.util.Date

data class MoodEntry(
    val id: Long = 0L,
    val moodType: String,
    val moodEmoji: String,
    val note: String = "",
    val date: Date = Date()
)