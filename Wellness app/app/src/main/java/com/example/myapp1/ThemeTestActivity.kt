package com.example.myapp1

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.myapp1.databinding.ActivityThemeTestBinding

class ThemeTestActivity : AppCompatActivity() {

    private lateinit var binding: ActivityThemeTestBinding
    
    override fun onCreate(savedInstanceState: Bundle?) {
        // Apply theme before setting content view
        ThemeManager.initializeTheme(this)
        
        super.onCreate(savedInstanceState)
        binding = ActivityThemeTestBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.btnLightTheme.setOnClickListener {
            ThemeManager.applyTheme(this, ThemeManager.THEME_LIGHT)
            Toast.makeText(this, "Applied Light Theme", Toast.LENGTH_SHORT).show()
        }

        binding.btnDarkTheme.setOnClickListener {
            ThemeManager.applyTheme(this, ThemeManager.THEME_DARK)
            Toast.makeText(this, "Applied Dark Theme", Toast.LENGTH_SHORT).show()
        }

        binding.btnSystemTheme.setOnClickListener {
            ThemeManager.applyTheme(this, ThemeManager.THEME_SYSTEM)
            Toast.makeText(this, "Applied System Theme", Toast.LENGTH_SHORT).show()
        }

        binding.btnBack.setOnClickListener {
            finish()
        }
    }
}