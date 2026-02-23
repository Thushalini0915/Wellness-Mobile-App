package com.example.myapp1

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp1.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var savedInstanceState: Bundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme before setting content view
        ThemeManager.initializeTheme(this)
        
        super.onCreate(savedInstanceState)
        this.savedInstanceState = savedInstanceState
        
        // Check authentication status
        if (!AuthManager.isLoggedIn(this)) {
            AuthManager.redirectToSignInIfNotAuthenticated(this)
            return
        }
        
        initializeUI()
    }
    
    private fun initializeUI() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        
        // Setup navigation after view is created
        setupNavigation()
        
        // Set the initial fragment
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, HomeFragment())
                .commit()
        }
    }

    private fun setupNavigation() {
        // Use manual navigation to avoid NavigationUI issues
        setupManualNavigation()
    }
    
    private fun setupManualNavigation() {
        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    // Navigate to home fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HomeFragment())
                        .commit()
                    true
                }
                R.id.navigation_habits -> {
                    // Navigate to habits fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, HabitsFragment())
                        .commit()
                    true
                }
                R.id.navigation_mood -> {
                    // Navigate to mood fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, MoodFragment())
                        .commit()
                    true
                }
                R.id.navigation_water -> {
                    // Navigate to water fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, WaterTrackingFragment())
                        .commit()
                    true
                }
                R.id.navigation_settings -> {
                    // Navigate to settings fragment
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragment_container, SettingsFragment())
                        .commit()
                    true
                }
                else -> false
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        // Since we're using manual navigation, just call the parent implementation
        return super.onSupportNavigateUp()
    }
}
