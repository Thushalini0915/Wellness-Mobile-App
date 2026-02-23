package com.example.myapp1

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp1.databinding.ActivitySignUpBinding

class SignUpActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Apply theme before setting content view
        ThemeManager.initializeTheme(this)
        
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnSignUp.setOnClickListener {
            signUp()
        }

        binding.btnSignIn.setOnClickListener {
            startActivity(Intent(this, SignInActivity::class.java))
            finish()
        }
    }

    private fun signUp() {
        val name = binding.etName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val password = binding.etPassword.text.toString().trim()
        val confirmPassword = binding.etConfirmPassword.text.toString().trim()

        if (validateInput(name, email, password, confirmPassword)) {
            // Show loading state
            binding.btnSignUp.isEnabled = false
            binding.btnSignUp.text = "Creating Account..."

            // Simulate account creation (replace with actual authentication logic)
            performSignUp(name, email, password)
        }
    }

    private fun validateInput(name: String, email: String, password: String, confirmPassword: String): Boolean {
        if (name.isEmpty()) {
            binding.etName.error = "Name is required"
            return false
        }

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

        if (confirmPassword.isEmpty()) {
            binding.etConfirmPassword.error = "Please confirm your password"
            return false
        }

        if (password != confirmPassword) {
            binding.etConfirmPassword.error = "Passwords do not match"
            return false
        }

        return true
    }

    private fun performSignUp(name: String, email: String, password: String) {
        // Simulate network delay
        binding.root.postDelayed({
            // For now, just simulate successful sign up
            // In a real app, you would call your authentication API here
            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Save user session using AuthManager
                AuthManager.saveUserLogin(this, email, name)

                // Show welcome message
                val welcomeMessage = GreetingManager.getWelcomeMessage(name)
                Toast.makeText(this, welcomeMessage, Toast.LENGTH_LONG).show()

                // Navigate to main activity
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
            } else {
                // Show error
                binding.btnSignUp.isEnabled = true
                binding.btnSignUp.text = "Sign Up"
                Toast.makeText(this, "Failed to create account. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }, 1500)
    }
}
