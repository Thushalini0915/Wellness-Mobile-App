package com.example.myapp1

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.example.myapp1.databinding.DialogAddWaterBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class AddWaterDialog : DialogFragment() {

    private var _binding: DialogAddWaterBinding? = null
    private val binding get() = _binding!!

    companion object {
        fun newInstance(): AddWaterDialog {
            return AddWaterDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        _binding = DialogAddWaterBinding.inflate(layoutInflater)
        
        return MaterialAlertDialogBuilder(requireContext())
            .setTitle("Add Water Entry")
            .setView(binding.root)
            .setPositiveButton("Add") { _, _ ->
                addWaterEntry()
            }
            .setNegativeButton("Cancel", null)
            .create()
    }

    private fun addWaterEntry() {
        val amountText = binding.etAmount.text.toString()
        val note = binding.etNote.text.toString()
        
        if (amountText.isNotEmpty()) {
            val amount = amountText.toIntOrNull() ?: 0
            if (amount > 0) {
                val waterEntry = WaterEntry(
                    id = System.currentTimeMillis(),
                    amount = amount,
                    date = java.util.Date(),
                    note = note
                )
                
                val sharedPreferencesManager = SharedPreferencesManager.getInstance()
                val allEntries = sharedPreferencesManager.getWaterEntries().toMutableList()
                allEntries.add(waterEntry)
                sharedPreferencesManager.saveWaterEntries(allEntries)
                
                // Notify parent fragment to refresh
                (parentFragment as? WaterTrackingFragment)?.let {
                    it.loadWaterEntries()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
