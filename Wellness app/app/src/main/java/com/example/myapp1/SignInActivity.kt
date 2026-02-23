package com.example.myapp1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp1.databinding.ActivitySignInBinding

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply theme before setting content view
        ThemeManager.initializeTheme(this)
        
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSignIn.setOnClickListener {
            signIn()
        }

        binding.btnSignUp.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }

        binding.btnForgotPassword.setOnClickListener {
            // Fill in test credentials for debugging
            binding.etEmail.setText("test@example.com")
            binding.etPassword.setText("password123")
            Toast.makeText(this, "Test credentials filled. Try signing in now.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun signIn() {
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()

        if (validateInput(email, password)) {
            // Show loading state
            binding.btnSignIn.isEnabled = false
            binding.btnSignIn.text = "Signing In..."

            // Simulate authentication (replace with actual authentication logic)
            performSignIn(email, password)
        }
    }

    private fun validateInput(email: String, password: String): Boolean {
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return false
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.etEmail.error = "Please enter a valid email"
            return false
        }

        if (password.isEmpty()) {
            binding.etPassword.error = "Password is required"
            return false
        }

        if (password.length < 6) {
            binding.etPassword.error = "Password must be at least 6 characters"
            return false
        }

        return true
    }

    private fun performSignIn(email: String, password: String) {
        // Add detailed debug logging
        android.util.Log.d("SignInActivity", "Attempting sign in with email: '$email'")
        android.util.Log.d("SignInActivity", "Password length: ${password.length}")
        android.util.Log.d("SignInActivity", "Email matches 'test@example.com': ${email == "test@example.com"}")
        android.util.Log.d("SignInActivity", "Password matches 'password123': ${password == "password123"}")
        
        // Simulate network delay
        binding.root.postDelayed({
            // For now, just simulate successful sign in
            // In a real app, you would call your authentication API here
            val validCredentials = listOf(
                "test@example.com" to "password123",
                "admin@wellness.com" to "admin123",
                "user@test.com" to "user123"
            )
            
            val isValidCredential = validCredentials.any { (validEmail, validPassword) ->
                email.equals(validEmail, ignoreCase = true) && password == validPassword
            }
            
            if (isValidCredential) {
                android.util.Log.d("SignInActivity", "Sign in successful")
                
                // Save user session using AuthManager
                AuthManager.saveUserLogin(this, email, "Test User")

                // Show personalized welcome message
                val welcomeMessage = GreetingManager.getPersonalizedGreeting("Test User")
                Toast.makeText(this, welcomeMessage, Toast.LENGTH_SHORT).show()

                // Navigate to main activity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                android.util.Log.d("SignInActivity", "Sign in failed - invalid credentials")
                android.util.Log.d("SignInActivity", "Expected: test@example.com, Got: '$email'")
                android.util.Log.d("SignInActivity", "Expected: password123, Got: '$password'")
                
                // Show error
                binding.btnSignIn.isEnabled = true
                binding.btnSignIn.text = "Sign In"
                Toast.makeText(this, "Invalid email or password", Toast.LENGTH_SHORT).show()
            }
        }, 1500)
    }
}
