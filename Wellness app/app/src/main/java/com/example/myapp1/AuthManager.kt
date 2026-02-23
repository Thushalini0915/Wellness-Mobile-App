package com.example.myapp1

import android.content.Context
import android.content.Intent


object AuthManager {
    
    private const val PREF_NAME = "WellnessApp"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_NAME = "user_name"
    

    fun isLoggedIn(context: Context): Boolean {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = sharedPrefs.getBoolean(KEY_IS_LOGGED_IN, false)
        android.util.Log.d("AuthManager", "isLoggedIn: $isLoggedIn")
        return isLoggedIn
    }
    

    fun getCurrentUserEmail(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return if (isLoggedIn(context)) {
            sharedPrefs.getString(KEY_USER_EMAIL, null)
        } else {
            null
        }
    }
    

    fun getCurrentUserName(context: Context): String? {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return if (isLoggedIn(context)) {
            sharedPrefs.getString(KEY_USER_NAME, null)
        } else {
            null
        }
    }
    

    fun saveUserLogin(context: Context, email: String, name: String? = null) {
        android.util.Log.d("AuthManager", "Saving user login: email=$email, name=$name")
        
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, true)
            .putString(KEY_USER_EMAIL, email)
            .apply()
        
        if (name != null) {
            sharedPrefs.edit()
                .putString(KEY_USER_NAME, name)
                .apply()
        }
        
        android.util.Log.d("AuthManager", "User login saved successfully")
    }
    

    fun logout(context: Context) {
        val sharedPrefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        sharedPrefs.edit()
            .putBoolean(KEY_IS_LOGGED_IN, false)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_NAME)
            .apply()
    }
    

    fun redirectBasedOnAuthStatus(context: Context, targetActivity: Class<*>) {
        if (isLoggedIn(context)) {
            // User is logged in, go to target activity
            val intent = Intent(context, targetActivity)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        } else {
            // User is not logged in, go to sign in
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }

    fun redirectToSignInIfNotAuthenticated(context: Context) {
        if (!isLoggedIn(context)) {
            val intent = Intent(context, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }
    }
}
