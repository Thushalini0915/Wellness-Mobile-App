package com.example.myapp1

import java.util.Date

data class WaterEntry(
    val id: Long = 0L,
    val amount: Int, // in ml
    val date: Date = Date(),
    val note: String = ""
)
