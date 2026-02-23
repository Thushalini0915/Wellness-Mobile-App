package com.example.myapp1

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapp1.databinding.FragmentSettingsBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    private lateinit var notificationManager: NotificationManager
    private var isInitialized = false

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        initializeComponents()
    }
    
    private fun initializeComponents() {
        try {
            // Initialize SharedPreferencesManager
            sharedPreferencesManager = try {
                SharedPreferencesManager.getInstance()
            } catch (e: IllegalStateException) {
                Log.w("SettingsFragment", "SharedPreferencesManager not initialized, creating new instance")
                SharedPreferencesManager.initialize(requireContext().applicationContext)
                SharedPreferencesManager.getInstance()
            }
            
            notificationManager = requireContext().getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            
            // Create notification channel if needed
            NotificationHelper.createNotificationChannel(requireContext())
            
            isInitialized = true
            
            setupUi()
            setupClickListeners()
            loadSettings()
            
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error initializing components", e)
            showSnackbar("Error initializing settings. Please try again.")
        }
    }

    private fun setupUi() {
        // Set up the slider values for demo (1-minute intervals)
        binding.sliderInterval.valueFrom = 1f
        binding.sliderInterval.valueTo = 180f
        binding.sliderInterval.stepSize = 1f
    }

    private fun setupClickListeners() {
        if (!isInitialized) {
            Log.w("SettingsFragment", "Components not initialized, skipping click listeners setup")
            return
        }
        
        // Hydration reminder toggle
        binding.switchHydrationReminder.setOnCheckedChangeListener { _, isChecked ->
            try {
                Log.d("SettingsFragment", "Hydration reminder toggled: $isChecked")
                
                sharedPreferencesManager.setHydrationReminderEnabled(isChecked)
                binding.layoutReminderSettings.visibility = if (isChecked) View.VISIBLE else View.GONE

                if (isChecked) {
                    val interval = binding.sliderInterval.value.toLong()
                    Log.d("SettingsFragment", "Scheduling reminder with interval: $interval minutes")
                    
                    sharedPreferencesManager.setHydrationReminderInterval(interval.toInt())
                    HydrationReminderManager.scheduleReminder(requireContext(), interval)
                    showSnackbar("Hydration reminders enabled for every $interval minutes")
                } else {
                    Log.d("SettingsFragment", "Cancelling hydration reminders")
                    HydrationReminderManager.cancelReminder(requireContext())
                    showSnackbar("Hydration reminders disabled")
                }
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error toggling hydration reminder", e)
                showSnackbar("Error updating reminder settings")
            }
        }

        // Interval slider
        binding.sliderInterval.addOnChangeListener { _, value, fromUser ->
            val minutes = value.toInt()
            binding.tvIntervalValue.text = "$minutes"

            // Only update if user changed it and reminders are enabled
            if (fromUser && binding.switchHydrationReminder.isChecked) {
                try {
                    Log.d("SettingsFragment", "Updating reminder interval to: $minutes minutes")
                    
                    sharedPreferencesManager.setHydrationReminderInterval(minutes)
                    HydrationReminderManager.scheduleReminder(requireContext(), minutes.toLong())
                    
                    val timeText = if (minutes == 1) "1 minute" else "$minutes minutes"
                    showSnackbar("Reminder interval updated to $timeText")
                } catch (e: Exception) {
                    Log.e("SettingsFragment", "Error updating reminder interval", e)
                    showSnackbar("Error updating reminder interval")
                }
            }
        }

        // Dark mode toggle
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            if (isAdded && activity != null) {
                try {
                    val theme = if (isChecked) ThemeManager.THEME_DARK else ThemeManager.THEME_LIGHT
                    val themeName = if (isChecked) "Dark mode" else "Light mode"
                    Log.d("SettingsFragment", "Changing theme to: $themeName")
                    
                    changeTheme(theme, themeName)
                } catch (e: Exception) {
                    Log.e("SettingsFragment", "Error changing theme", e)
                    showSnackbar("Error changing theme")
                }
            }
        }

        // Theme test button (for debugging)
        binding.btnThemeTest?.setOnClickListener {
            try {
                val intent = Intent(requireContext(), ThemeTestActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error starting theme test activity", e)
                showSnackbar("Theme test not available")
            }
        }

        // Profile settings button
        binding.btnProfileSettings?.setOnClickListener {
            try {
                val intent = Intent(requireContext(), ProfileSettingsActivity::class.java)
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error starting profile settings activity", e)
                showSnackbar("Profile settings not available")
            }
        }

        // Test notification button (for demo purposes)
        binding.btnThemeTest?.setOnClickListener {
            Log.d("SettingsFragment", "🚀 ULTRA AGGRESSIVE NOTIFICATION TEST!")
            try {
                // Method 1: Show immediate notification
                NotificationHelper.showHydrationReminder(requireContext())
                Log.d("SettingsFragment", "📱 IMMEDIATE notification sent!")
                showSnackbar("✅ Immediate test sent!")
                
                // Method 2: Handler-based 3-second test
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded && activity != null) {
                        NotificationHelper.showHydrationReminder(requireContext())
                        Log.d("SettingsFragment", "⏰ HANDLER: 3-second test notification sent!")
                        showSnackbar("🎉 3-second Handler test worked!")
                    }
                }, 3000)
                
                // Method 3: Coroutine-based 6-second test
                lifecycleScope.launch {
                    try {
                        kotlinx.coroutines.delay(6000) // 6 seconds
                        if (isAdded && activity != null) {
                            NotificationHelper.showHydrationReminder(requireContext())
                            Log.d("SettingsFragment", "🔄 COROUTINE: 6-second test notification sent!")
                            showSnackbar("🚀 6-second Coroutine test worked!")
                        }
                    } catch (e: Exception) {
                        Log.e("SettingsFragment", "Error in coroutine test", e)
                    }
                }
                
                // Method 4: Force multiple notifications
                repeat(3) { i ->
                    Handler(Looper.getMainLooper()).postDelayed({
                        if (isAdded && activity != null) {
                            NotificationHelper.showHydrationReminder(requireContext())
                            Log.d("SettingsFragment", "💥 FORCE TEST #${i+1} sent!")
                        }
                    }, (i + 1) * 1000L) // 1, 2, 3 seconds
                }
                
                showSnackbar("🚀 ULTRA AGGRESSIVE TEST: Multiple notifications incoming!")
                
            } catch (e: Exception) {
                Log.e("SettingsFragment", "Error in ultra aggressive test", e)
                showSnackbar("Error in test - check logs")
            }
        }


        // Logout button
        binding.btnLogout.setOnClickListener {
            Log.d("SettingsFragment", "Logout button clicked")
            showLogoutConfirmation()
        }
    }

    private fun loadSettings() {
        if (!isInitialized) {
            Log.w("SettingsFragment", "Components not initialized, skipping settings load")
            return
        }
        
        try {
            Log.d("SettingsFragment", "Loading settings...")
            
            // Load hydration reminder settings
            val isReminderEnabled = sharedPreferencesManager.isHydrationReminderEnabled()
            binding.switchHydrationReminder.isChecked = isReminderEnabled
            binding.layoutReminderSettings.visibility = if (isReminderEnabled) View.VISIBLE else View.GONE
            
            Log.d("SettingsFragment", "Hydration reminder enabled: $isReminderEnabled")

            // Load reminder interval
            val interval = sharedPreferencesManager.getHydrationReminderInterval()
            // Ensure interval is within slider bounds (1-180) for demo
            val validInterval = interval.coerceIn(1, 180)
            binding.sliderInterval.value = validInterval.toFloat()
            binding.tvIntervalValue.text = "$validInterval"
            
            // Update stored value if it was corrected
            if (validInterval != interval) {
                sharedPreferencesManager.setHydrationReminderInterval(validInterval)
                Log.d("SettingsFragment", "Corrected interval from $interval to $validInterval minutes")
            }
            
            Log.d("SettingsFragment", "Reminder interval: $interval minutes")

            // Load theme settings
            val currentTheme = ThemeManager.getCurrentTheme(requireContext())
            val isDarkMode = when (currentTheme) {
                ThemeManager.THEME_DARK -> true
                ThemeManager.THEME_LIGHT -> false
                ThemeManager.THEME_SYSTEM -> ThemeManager.isDarkTheme(requireContext())
                else -> false
            }
            binding.switchDarkMode.isChecked = isDarkMode
            
            Log.d("SettingsFragment", "Current theme: $currentTheme, Dark mode: $isDarkMode")

            // Load user information
            val userName = AuthManager.getCurrentUserName(requireContext())
            val userEmail = AuthManager.getCurrentUserEmail(requireContext())
            val isLoggedIn = AuthManager.isLoggedIn(requireContext())
            
            Log.d("SettingsFragment", "User logged in: $isLoggedIn, Name: $userName, Email: $userEmail")

            binding.tvUserEmail?.text = when {
                !isLoggedIn -> "Not logged in"
                !userName.isNullOrEmpty() -> "$userName\n$userEmail"
                !userEmail.isNullOrEmpty() -> userEmail
                else -> "Guest User"
            }
            
            // Enable/disable logout button based on login status
            binding.btnLogout.isEnabled = isLoggedIn
            binding.btnLogout.alpha = if (isLoggedIn) 1.0f else 0.5f
            
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error loading settings", e)
            showSnackbar("Error loading settings")
        }
    }

    private fun showLogoutConfirmation() {
        try {
            Log.d("SettingsFragment", "Showing logout confirmation dialog")
            
            // Check if user is actually logged in
            if (!AuthManager.isLoggedIn(requireContext())) {
                showSnackbar("You are not logged in")
                return
            }
            
            MaterialAlertDialogBuilder(requireContext())
                .setTitle("Sign Out")
                .setMessage("Are you sure you want to sign out? You will need to log in again to access your data.")
                .setPositiveButton("Sign Out") { _, _ ->
                    performLogout()
                }
                .setNegativeButton("Cancel", null)
                .setCancelable(true)
                .show()
                
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error showing logout confirmation", e)
            showSnackbar("Error showing logout dialog")
        }
    }

    private fun performLogout() {
        try {
            Log.d("SettingsFragment", "Performing logout...")
            
            // Show loading state
            binding.btnLogout.isEnabled = false
            binding.btnLogout.text = "Signing out..."
            
            lifecycleScope.launch {
                try {
                    // Add a small delay to show the loading state
                    delay(500)
                    
                    // Clear authentication data
                    AuthManager.logout(requireContext())
                    
                    // Cancel any active reminders
                    HydrationReminderManager.cancelReminder(requireContext())
                    
                    Log.d("SettingsFragment", "Logout successful, redirecting to sign in")
                    
                    // Show confirmation
                    showSnackbar("Signed out successfully")
                    
                    // Small delay before navigation
                    delay(1000)
                    
                    // Check if fragment is still attached
                    if (isAdded && activity != null) {
                        // Redirect to sign in
                        val intent = Intent(requireContext(), SignInActivity::class.java).apply {
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                        }
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    
                } catch (e: Exception) {
                    Log.e("SettingsFragment", "Error during logout process", e)
                    
                    // Reset button state
                    if (isAdded) {
                        binding.btnLogout.isEnabled = true
                        binding.btnLogout.text = "Logout"
                        showSnackbar("Error during logout. Please try again.")
                    }
                }
            }
            
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error initiating logout", e)
            showSnackbar("Error during logout. Please try again.")
            
            // Reset button state
            binding.btnLogout.isEnabled = true
            binding.btnLogout.text = "Logout"
        }
    }

    private var themeChangeInProgress = false
    private var lastThemeChangeTime = 0L
    private val THEME_CHANGE_DEBOUNCE = 1000L // 1 second debounce

    private fun changeTheme(themeMode: String, themeName: String) {
        val currentTime = System.currentTimeMillis()

        if (themeChangeInProgress) {
            return // Prevent multiple simultaneous theme changes
        }

        if (currentTime - lastThemeChangeTime < THEME_CHANGE_DEBOUNCE) {
            showSnackbar("Please wait before changing theme again")
            return // Debounce rapid theme changes
        }

        try {
            themeChangeInProgress = true
            lastThemeChangeTime = currentTime
            ThemeManager.applyTheme(requireContext(), themeMode)
            showSnackbar("Theme changed to $themeName")

            // Use a more efficient recreation approach
            recreateActivitySafely()
        } catch (e: Exception) {
            showSnackbar("Failed to change theme. Please try again.")
            themeChangeInProgress = false
        }
    }

    private fun recreateActivitySafely() {
        try {
            // Use a longer delay to prevent rapid recreations
            binding.root.postDelayed({
                if (isAdded && !isDetached && !themeChangeInProgress) {
                    try {
                        requireActivity().recreate()
                    } catch (e: Exception) {
                        showSnackbar("Theme changed. Please restart the app to see changes.")
                    } finally {
                        themeChangeInProgress = false
                    }
                } else {
                    themeChangeInProgress = false
                }
            }, 500) // Increased delay to prevent rapid recreations
        } catch (e: Exception) {
            showSnackbar("Theme changed. Please restart the app to see changes.")
            themeChangeInProgress = false
        }
    }

    private fun showSnackbar(message: String) {
        try {
            if (isAdded && view != null) {
                Snackbar.make(binding.root, message, Snackbar.LENGTH_SHORT).show()
            }
        } catch (e: Exception) {
            Log.e("SettingsFragment", "Error showing snackbar: $message", e)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        themeChangeInProgress = false
        isInitialized = false
        _binding = null
    }

    companion object {
        fun newInstance() = SettingsFragment()
    }
}
