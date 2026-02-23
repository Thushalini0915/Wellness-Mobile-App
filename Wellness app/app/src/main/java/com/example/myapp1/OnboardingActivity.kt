package com.example.myapp1

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.myapp1.databinding.ActivityOnboardingBinding
import com.google.android.material.button.MaterialButton

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: OnboardingAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Check if onboarding is already completed
        if (WellnessApp.isOnboardingCompleted(this)) {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }
        
        // Apply theme before setting content view
        ThemeManager.initializeTheme(this)
        
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupViewPager()
        setupClickListeners()
    }

    private fun setupViewPager() {
        viewPager = binding.viewPager
        adapter = OnboardingAdapter(this)
        viewPager.adapter = adapter

        // Add page change listener to update button text and indicators
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUI(position)
                animatePageTransition(position)
            }
        })

        // Set up page indicators
        setupPageIndicators()
    }

    private fun setupPageIndicators() {
        val indicators = listOf(
            binding.indicator1,
            binding.indicator2,
            binding.indicator3,
            binding.indicator4
        )

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                indicators.forEachIndexed { index, indicator ->
                    indicator.isSelected = index == position
                }
            }
        })
    }

    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            if (viewPager.currentItem < adapter.itemCount - 1) {
                viewPager.currentItem = viewPager.currentItem + 1
            } else {
                finishOnboarding()
            }
        }

        binding.btnSkip.setOnClickListener {
            finishOnboarding()
        }
    }

    private fun updateUI(position: Int) {
        when (position) {
            0, 1, 2 -> {
                binding.btnNext.text = getString(R.string.onboarding_next)
                binding.btnSkip.visibility = MaterialButton.VISIBLE
            }
            3 -> {
                binding.btnNext.text = getString(R.string.onboarding_get_started)
                binding.btnSkip.visibility = MaterialButton.GONE
            }
        }
    }

    private fun animatePageTransition(position: Int) {
        // Animate the bottom container
        binding.bottomContainer.alpha = 0.7f
        binding.bottomContainer.animate()
            .alpha(1.0f)
            .setDuration(300)
            .start()
    }

    private fun finishOnboarding() {
        // Mark onboarding as completed
        val sharedPrefs = getSharedPreferences("WellnessApp", MODE_PRIVATE)
        sharedPrefs.edit().putBoolean("onboarding_completed", true).apply()

        // Animate the finish button
        binding.btnNext.animate()
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(150)
            .withEndAction {
                binding.btnNext.animate()
                    .scaleX(1.0f)
                    .scaleY(1.0f)
                    .setDuration(150)
                    .withEndAction {
                        // Redirect to sign in if not authenticated, otherwise to main activity
                        if (AuthManager.isLoggedIn(this@OnboardingActivity)) {
                            val intent = Intent(this@OnboardingActivity, MainActivity::class.java)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this@OnboardingActivity, SignInActivity::class.java)
                            startActivity(intent)
                        }
                        finish()
                    }
                    .start()
            }
            .start()
    }

    private inner class OnboardingAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun getItemCount(): Int = 4

        override fun createFragment(position: Int): Fragment {
            return OnboardingFragment.newInstance(position)
        }
    }
}
