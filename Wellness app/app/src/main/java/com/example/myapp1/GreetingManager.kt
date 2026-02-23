package com.example.myapp1

import java.util.Calendar


object GreetingManager {
    

    fun getTimeBasedGreeting(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        return when (hour) {
            in 5..11 -> "Good morning"
            in 12..17 -> "Good afternoon"
            in 18..21 -> "Good evening"
            else -> "Good night"
        }
    }
    

    fun getPersonalizedGreeting(userName: String? = null): String {
        val timeGreeting = getTimeBasedGreeting()
        return if (userName != null && userName.isNotEmpty()) {
            "$timeGreeting, $userName!"
        } else {
            "$timeGreeting!"
        }
    }
    

    fun getWelcomeMessage(userName: String): String {
        val timeGreeting = getTimeBasedGreeting()
        return "Welcome, $userName! $timeGreeting and thank you for joining our wellness journey."
    }
    

    fun getMotivationalMessage(): String {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        
        return when (hour) {
            in 5..11 -> "Let's start your day with positive energy!"
            in 12..17 -> "Keep up the great work this afternoon!"
            in 18..21 -> "Time to wind down and reflect on your day."
            else -> "Rest well and recharge for tomorrow!"
        }
    }
}
