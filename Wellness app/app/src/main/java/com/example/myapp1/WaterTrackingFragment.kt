package com.example.myapp1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.coroutines.launch
import com.example.myapp1.databinding.FragmentWaterTrackingBinding
import com.google.android.material.snackbar.Snackbar
import java.util.*

class WaterTrackingFragment : Fragment() {

    private var _binding: FragmentWaterTrackingBinding? = null
    private val binding get() = _binding!!
    
    private lateinit var waterAdapter: WaterAdapter
    private val waterEntries = mutableListOf<WaterEntry>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWaterTrackingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupRecyclerView()
        loadWaterEntries()
        setupClickListeners()
    }
    
    private fun setupRecyclerView() {
        waterAdapter = WaterAdapter { waterEntry ->
            deleteWaterEntry(waterEntry)
        }
        
        binding.recyclerWaterEntries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = waterAdapter
            setHasFixedSize(true)
        }
    }
    
    private fun setupClickListeners() {
        binding.fabAddWater.setOnClickListener {
            showAddWaterDialog()
        }
        
        // Quick add buttons
        binding.btnAdd250ml.setOnClickListener {
            addWaterEntry(250)
        }
        
        binding.btnAdd500ml.setOnClickListener {
            addWaterEntry(500)
        }
        
        binding.btnAdd750ml.setOnClickListener {
            addWaterEntry(750)
        }
    }
    
    fun loadWaterEntries() {
        val sharedPreferencesManager = SharedPreferencesManager.getInstance()
        val entries = sharedPreferencesManager.getWaterEntries()
        val today = Calendar.getInstance()
        
        // Filter entries for today only
        val todayEntries = entries.filter { entry ->
            val entryCalendar = Calendar.getInstance()
            entryCalendar.time = entry.date
            today.get(Calendar.YEAR) == entryCalendar.get(Calendar.YEAR) &&
            today.get(Calendar.MONTH) == entryCalendar.get(Calendar.MONTH) &&
            today.get(Calendar.DAY_OF_MONTH) == entryCalendar.get(Calendar.DAY_OF_MONTH)
        }
        
        waterEntries.clear()
        waterEntries.addAll(todayEntries.sortedByDescending { it.date })
        waterAdapter.submitList(waterEntries.toList())
        
        updateWaterStats()
        
        // Show empty state if no water entries
        if (waterEntries.isEmpty()) {
            binding.emptyState.root.visibility = View.VISIBLE
            binding.recyclerWaterEntries.visibility = View.GONE
            binding.waterStatsCard.visibility = View.GONE
        } else {
            binding.emptyState.root.visibility = View.GONE
            binding.recyclerWaterEntries.visibility = View.VISIBLE
            binding.waterStatsCard.visibility = View.VISIBLE
        }
    }
    
    private fun updateWaterStats() {
        val totalWater = waterEntries.sumOf { it.amount }
        val targetWater = 2000 // 2 liters target
        
        binding.tvTotalWater.text = "${totalWater}ml"
        binding.tvTargetWater.text = "${targetWater}ml"
        
        val progress = if (targetWater > 0) {
            (totalWater.toFloat() / targetWater * 100).toInt()
        } else 0
        
        binding.progressBarWater.progress = progress
        binding.tvProgressPercentage.text = "${progress}%"
        
        // Update progress bar color based on completion
        if (progress >= 100) {
            binding.progressBarWater.setIndicatorColor(requireContext().getColor(R.color.success))
        } else if (progress >= 50) {
            binding.progressBarWater.setIndicatorColor(requireContext().getColor(R.color.warning))
        } else {
            binding.progressBarWater.setIndicatorColor(requireContext().getColor(R.color.error))
        }
    }
    
    private fun addWaterEntry(amount: Int) {
        val newEntry = WaterEntry(
            id = System.currentTimeMillis(),
            amount = amount,
            date = Date()
        )
        
        val sharedPreferencesManager = SharedPreferencesManager.getInstance()
        val allEntries = sharedPreferencesManager.getWaterEntries().toMutableList()
        allEntries.add(newEntry)
        sharedPreferencesManager.saveWaterEntries(allEntries)
        
        loadWaterEntries()
        
        Snackbar.make(
            binding.root,
            "Added ${amount}ml of water",
            Snackbar.LENGTH_SHORT
        ).show()
    }
    
    private fun deleteWaterEntry(waterEntry: WaterEntry) {
        val sharedPreferencesManager = SharedPreferencesManager.getInstance()
        val allEntries = sharedPreferencesManager.getWaterEntries().toMutableList()
        allEntries.removeIf { it.id == waterEntry.id }
        sharedPreferencesManager.saveWaterEntries(allEntries)
        
        loadWaterEntries()
        
        Snackbar.make(
            binding.root,
            "Water entry deleted",
            Snackbar.LENGTH_SHORT
        ).show()
    }
    
    private fun showAddWaterDialog() {
        val dialog = AddWaterDialog.newInstance()
        dialog.show(parentFragmentManager, "AddWaterDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
