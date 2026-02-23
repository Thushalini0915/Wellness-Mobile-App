package com.example.myapp1

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.myapp1.databinding.FragmentHomeBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class HomeFragment : Fragment(), StepCounterManager.StepCountListener {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var stepCounterManager: StepCounterManager
    private lateinit var sharedPreferencesManager: SharedPreferencesManager
    
    private val stepDataList = mutableListOf<StepData>()
    private val dateFormat = SimpleDateFormat("MMM dd", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        sharedPreferencesManager = SharedPreferencesManager.getInstance()
        stepCounterManager = StepCounterManager(requireContext())
        
        setupViews()
        loadData()
        setupStepCounter()
        setupCharts()
    }
    
    private fun setupViews() {
        binding.apply {
            // Set up refresh listener
            swipeRefreshLayout.setOnRefreshListener {
                loadData()
                swipeRefreshLayout.isRefreshing = false
            }
            
            // Set up personalized greeting
            setupPersonalizedGreeting()
        }
    }
    
    private fun setupPersonalizedGreeting() {
        val userName = AuthManager.getCurrentUserName(requireContext())
        val userEmail = AuthManager.getCurrentUserEmail(requireContext())
        
        binding.apply {
            // Set time-based greeting
            tvGreeting.text = GreetingManager.getTimeBasedGreeting()
            
            // Set personalized message
            if (userName != null && userName.isNotEmpty()) {
                tvUserName.text = "Welcome back, $userName!"
            } else if (userEmail != null) {
                // Extract name from email if no name is available
                val nameFromEmail = userEmail.substringBefore("@").replace(".", " ").split(" ").joinToString(" ") { 
                    it.replaceFirstChar { char -> char.uppercaseChar() }
                }
                tvUserName.text = "Welcome back, $nameFromEmail!"
            } else {
                tvUserName.text = "Welcome back!"
            }
            
            // Set motivational message
            tvMotivationalMessage.text = GreetingManager.getMotivationalMessage()
        }
    }
    
    private fun loadData() {
        loadStepData()
        loadHabitsData()
        loadMoodData()
        loadWaterData()
    }
    
    private fun loadStepData() {
        stepDataList.clear()
        stepDataList.addAll(sharedPreferencesManager.getStepData())
        
        val today = Calendar.getInstance()
        val todayStepData = stepDataList.find { stepData ->
            val stepDate = Calendar.getInstance()
            stepDate.time = stepData.date
            today.get(Calendar.YEAR) == stepDate.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == stepDate.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == stepDate.get(Calendar.DAY_OF_MONTH)
        }
        
        val currentSteps = todayStepData?.steps ?: 0
        val dailyGoal = sharedPreferencesManager.getDailyStepGoal()
        
        binding.apply {
            tvStepsCount.text = currentSteps.toString()
            tvStepsGoal.text = "/ $dailyGoal"
            
            val progress = if (dailyGoal > 0) (currentSteps.toFloat() / dailyGoal * 100).toInt() else 0
            progressBarSteps.progress = progress
            tvStepsProgress.text = "$progress%"
            
            // Update progress bar color based on completion
            val progressColor = when {
                progress >= 100 -> ContextCompat.getColor(requireContext(), R.color.success)
                progress >= 50 -> ContextCompat.getColor(requireContext(), R.color.warning)
                else -> ContextCompat.getColor(requireContext(), R.color.error)
            }
            progressBarSteps.setIndicatorColor(progressColor)
        }
    }
    
    private fun loadHabitsData() {
        val habits = sharedPreferencesManager.getHabits()
        val completedToday = habits.count { it.isCompleted }
        
        binding.apply {
            tvHabitsCompleted.text = completedToday.toString()
            tvHabitsTotal.text = "/ ${habits.size}"
        }
    }
    
    private fun loadMoodData() {
        val moodEntries = sharedPreferencesManager.getMoodEntries()
        val today = Calendar.getInstance()
        
        val todayMood = moodEntries.find { entry ->
            val entryDate = Calendar.getInstance()
            entryDate.time = entry.date
            today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == entryDate.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == entryDate.get(Calendar.DAY_OF_MONTH)
        }
        
        binding.apply {
            if (todayMood != null) {
                tvMoodEmoji.text = todayMood.moodEmoji
                tvMoodText.text = "Today's Mood"
            } else {
                tvMoodEmoji.text = "😊"
                tvMoodText.text = "No mood logged"
            }
        }
    }
    
    private fun loadWaterData() {
        val waterEntries = sharedPreferencesManager.getWaterEntries()
        val today = Calendar.getInstance()
        
        val todayWater = waterEntries.filter { entry ->
            val entryDate = Calendar.getInstance()
            entryDate.time = entry.date
            today.get(Calendar.YEAR) == entryDate.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == entryDate.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == entryDate.get(Calendar.DAY_OF_MONTH)
        }.sumOf { it.amount }
        
        binding.apply {
            tvWaterAmount.text = "${todayWater}ml"
            tvWaterGoal.text = "/ 2000ml"
            
            val progress = if (2000 > 0) (todayWater.toFloat() / 2000 * 100).toInt() else 0
            progressBarWater.progress = progress
        }
    }
    
    private fun setupStepCounter() {
        if (checkStepPermission()) {
            stepCounterManager.addStepCountListener(this)
            stepCounterManager.startListening()
        } else {
            requestStepPermission()
        }
    }
    
    private fun checkStepPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            Manifest.permission.ACTIVITY_RECOGNITION
        ) == PackageManager.PERMISSION_GRANTED
    }
    
    private fun requestStepPermission() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACTIVITY_RECOGNITION),
            PERMISSION_REQUEST_CODE
        )
    }
    
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupStepCounter()
            } else {
                Toast.makeText(requireContext(), "Step counter permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
    
    private fun setupCharts() {
        setupStepsChart()
        setupHabitsChart()
    }
    
    private fun setupStepsChart() {
        val chart = binding.chartSteps
        
        // Get last 7 days of step data
        val calendar = Calendar.getInstance()
        val last7Days = mutableListOf<StepData>()
        
        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_MONTH, -i)
            val dayData = stepDataList.find { stepData ->
                val stepDate = Calendar.getInstance()
                stepDate.time = stepData.date
                calendar.get(Calendar.YEAR) == stepDate.get(Calendar.YEAR) &&
                calendar.get(Calendar.MONTH) == stepDate.get(Calendar.MONTH) &&
                calendar.get(Calendar.DAY_OF_MONTH) == stepDate.get(Calendar.DAY_OF_MONTH)
            }
            last7Days.add(dayData ?: StepData(steps = 0, date = calendar.time))
            calendar.add(Calendar.DAY_OF_MONTH, i) // Reset calendar
        }
        
        val entries = last7Days.mapIndexed { index, stepData ->
            Entry(index.toFloat(), stepData.steps.toFloat())
        }
        
        val dataSet = LineDataSet(entries, "Steps").apply {
            color = ContextCompat.getColor(requireContext(), R.color.primary)
            setCircleColor(ContextCompat.getColor(requireContext(), R.color.primary))
            lineWidth = 3f
            circleRadius = 4f
            setDrawValues(false)
        }
        
        val lineData = LineData(dataSet)
        chart.data = lineData
        
        // Configure chart
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(true)
            textSize = 12f
            valueFormatter = IndexAxisValueFormatter(last7Days.map { dateFormat.format(it.date) })
        }
        
        chart.axisLeft.apply {
            setDrawGridLines(true)
            setDrawAxisLine(true)
            axisMinimum = 0f
            textSize = 12f
            setDrawLabels(true)
        }
        
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        
        // Add extra offset to prevent clipping
        chart.setExtraOffsets(16f, 16f, 16f, 16f)
        
        chart.invalidate()
    }
    
    private fun setupHabitsChart() {
        val chart = binding.chartHabits
        
        // Get last 7 days of habit completion data
        val calendar = Calendar.getInstance()
        val last7Days = mutableListOf<Int>()
        
        for (i in 6 downTo 0) {
            calendar.add(Calendar.DAY_OF_MONTH, -i)
            // For simplicity, we'll use random data since we don't have historical habit completion
            last7Days.add((0..5).random())
            calendar.add(Calendar.DAY_OF_MONTH, i) // Reset calendar
        }
        
        val entries = last7Days.mapIndexed { index, completed ->
            BarEntry(index.toFloat(), completed.toFloat())
        }
        
        val dataSet = BarDataSet(entries, "Habits Completed").apply {
            color = ContextCompat.getColor(requireContext(), R.color.secondary)
        }
        
        val barData = BarData(dataSet)
        chart.data = barData
        
        // Configure chart
        chart.xAxis.apply {
            position = XAxis.XAxisPosition.BOTTOM
            setDrawGridLines(false)
            setDrawAxisLine(true)
            textSize = 12f
            valueFormatter = IndexAxisValueFormatter(
                (0..6).map { 
                    val date = Calendar.getInstance()
                    date.add(Calendar.DAY_OF_MONTH, -it)
                    dateFormat.format(date.time)
                }
            )
        }
        
        chart.axisLeft.apply {
            setDrawGridLines(true)
            setDrawAxisLine(true)
            axisMinimum = 0f
            textSize = 12f
            setDrawLabels(true)
        }
        
        chart.axisRight.isEnabled = false
        chart.description.isEnabled = false
        chart.legend.isEnabled = false
        chart.setTouchEnabled(true)
        chart.isDragEnabled = true
        chart.setScaleEnabled(true)
        chart.setPinchZoom(true)
        
        // Add extra offset to prevent clipping
        chart.setExtraOffsets(16f, 16f, 16f, 16f)
        
        chart.invalidate()
    }
    
    override fun onStepCountChanged(steps: Int) {
        lifecycleScope.launch {
            // Update UI on main thread
            binding.tvStepsCount.text = steps.toString()
            
            val dailyGoal = sharedPreferencesManager.getDailyStepGoal()
            val progress = if (dailyGoal > 0) (steps.toFloat() / dailyGoal * 100).toInt() else 0
            binding.progressBarSteps.progress = progress
            binding.tvStepsProgress.text = "$progress%"
            
            // Save step data
            val today = Calendar.getInstance()
            val existingData = stepDataList.find { stepData ->
                val stepDate = Calendar.getInstance()
                stepDate.time = stepData.date
                today.get(Calendar.YEAR) == stepDate.get(Calendar.YEAR) &&
                today.get(Calendar.MONTH) == stepDate.get(Calendar.MONTH) &&
                today.get(Calendar.DAY_OF_MONTH) == stepDate.get(Calendar.DAY_OF_MONTH)
            }
            
            if (existingData != null) {
                existingData.copy(steps = steps)
            } else {
                stepDataList.add(StepData(steps = steps, date = Date()))
            }
            
            sharedPreferencesManager.saveStepData(stepDataList)
            
            // Refresh charts with updated data
            setupStepsChart()
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        stepCounterManager.removeStepCountListener(this)
        stepCounterManager.stopListening()
        _binding = null
    }
    
    companion object {
        private const val PERMISSION_REQUEST_CODE = 1001
    }
}
