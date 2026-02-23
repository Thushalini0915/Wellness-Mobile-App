package com.example.myapp1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.myapp1.databinding.FragmentOnboardingBinding

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    companion object {
        private const val ARG_POSITION = "position"

        fun newInstance(position: Int): OnboardingFragment {
            val fragment = OnboardingFragment()
            val args = Bundle()
            args.putInt(ARG_POSITION, position)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val position = arguments?.getInt(ARG_POSITION) ?: 0
        setupContent(position)
        animateContent()
    }

    private fun setupContent(position: Int) {
        when (position) {
            0 -> {
                binding.apply {
                    ivOnboardingImage.setImageResource(R.drawable.ic_track_wellness)
                    tvOnboardingTitle.text = getString(R.string.onboarding_track_title)
                    tvOnboardingDescription.text = getString(R.string.onboarding_track_description)
                }
            }
            1 -> {
                binding.apply {
                    ivOnboardingImage.setImageResource(R.drawable.ic_insights)
                    tvOnboardingTitle.text = getString(R.string.onboarding_insights_title)
                    tvOnboardingDescription.text = getString(R.string.onboarding_insights_description)
                }
            }
            2 -> {
                binding.apply {
                    ivOnboardingImage.setImageResource(R.drawable.ic_goals)
                    tvOnboardingTitle.text = getString(R.string.onboarding_goals_title)
                    tvOnboardingDescription.text = getString(R.string.onboarding_goals_description)
                }
            }
            3 -> {
                binding.apply {
                    ivOnboardingImage.setImageResource(R.drawable.ic_features)
                    tvOnboardingTitle.text = getString(R.string.onboarding_features_title)
                    tvOnboardingDescription.text = getString(R.string.onboarding_features_description)
                }
            }
        }
    }

    private fun animateContent() {
        // Animate the image with scale and fade
        binding.ivOnboardingImage.animate()
            .scaleX(1.0f)
            .scaleY(1.0f)
            .alpha(1.0f)
            .setDuration(600)
            .setStartDelay(200)
            .start()

        // Animate the title with slide in from right
        binding.tvOnboardingTitle.alpha = 0f
        binding.tvOnboardingTitle.translationX = 100f
        binding.tvOnboardingTitle.animate()
            .alpha(1.0f)
            .translationX(0f)
            .setDuration(500)
            .setStartDelay(400)
            .start()

        // Animate the description with slide in from right
        binding.tvOnboardingDescription.alpha = 0f
        binding.tvOnboardingDescription.translationX = 100f
        binding.tvOnboardingDescription.animate()
            .alpha(1.0f)
            .translationX(0f)
            .setDuration(500)
            .setStartDelay(600)
            .start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
