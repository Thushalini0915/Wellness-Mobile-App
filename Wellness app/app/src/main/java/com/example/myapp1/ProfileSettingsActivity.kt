package com.example.myapp1

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.example.myapp1.databinding.ActivityProfileSettingsBinding
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ProfileSettingsActivity : AppCompatActivity() {
    
    private lateinit var binding: ActivityProfileSettingsBinding
    private var currentPhotoPath: String? = null
    
    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageUri = result.data?.data
            imageUri?.let {
                binding.ivProfileAvatar.setImageURI(it)
                // Save the image URI or path for persistence
                saveProfileImage(it.toString())
            }
        }
    }
    
    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            currentPhotoPath?.let { path ->
                val imageUri = Uri.fromFile(File(path))
                binding.ivProfileAvatar.setImageURI(imageUri)
                saveProfileImage(imageUri.toString())
            }
        }
    }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        setupToolbar()
        setupClickListeners()
        loadProfileData()
    }
    
    private fun setupToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Profile Settings"
        
        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }
    
    private fun setupClickListeners() {
        // Avatar click to change photo
        binding.ivProfileAvatar.setOnClickListener {
            showImageSourceDialog()
        }
        
        // Save button
        binding.btnSaveProfile.setOnClickListener {
            saveProfile()
        }
        
        // Change password button
        binding.btnChangePassword.setOnClickListener {
            showChangePasswordDialog()
        }
        
        // Edit profile button
        binding.btnEditProfile.setOnClickListener {
            toggleEditMode(true)
        }
        
        // Cancel edit button
        binding.btnCancelEdit.setOnClickListener {
            toggleEditMode(false)
            loadProfileData() // Reload original data
        }
    }
    
    private fun loadProfileData() {
        val userName = AuthManager.getCurrentUserName(this)
        val userEmail = AuthManager.getCurrentUserEmail(this)
        
        binding.apply {
            etFullName.setText(userName ?: "")
            etEmail.setText(userEmail ?: "")
            etPhone.setText(getStoredPhoneNumber())
            etBio.setText(getStoredBio())
            
            // Load profile image if available
            loadProfileImage()
        }
    }
    
    private fun saveProfile() {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phone = binding.etPhone.text.toString().trim()
        val bio = binding.etBio.text.toString().trim()
        
        if (fullName.isEmpty()) {
            binding.etFullName.error = "Full name is required"
            return
        }
        
        if (email.isEmpty()) {
            binding.etEmail.error = "Email is required"
            return
        }
        
        // Save user data
        AuthManager.saveUserLogin(this, email, fullName)
        saveStoredPhoneNumber(phone)
        saveStoredBio(bio)
        
        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
        toggleEditMode(false)
    }
    
    private fun showImageSourceDialog() {
        val options = arrayOf("Camera", "Gallery", "Remove Photo")
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Select Image Source")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> openCamera()
                    1 -> openGallery()
                    2 -> removeProfileImage()
                }
            }
            .show()
    }
    
    private fun openCamera() {
        try {
            val photoFile = createImageFile()
            val photoURI = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                photoFile
            )
            
            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }
            cameraLauncher.launch(cameraIntent)
        } catch (e: IOException) {
            Toast.makeText(this, "Error creating image file", Toast.LENGTH_SHORT).show()
        }
    }
    
    private fun openGallery() {
        val galleryIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        imagePickerLauncher.launch(galleryIntent)
    }
    
    private fun removeProfileImage() {
        binding.ivProfileAvatar.setImageResource(R.drawable.ic_person)
        saveProfileImage("")
        Toast.makeText(this, "Profile photo removed", Toast.LENGTH_SHORT).show()
    }
    
    private fun createImageFile(): File {
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir = getExternalFilesDir("Pictures")
        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir).apply {
            currentPhotoPath = absolutePath
        }
    }
    
    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_change_password, null)
        val etCurrentPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_current_password)
        val etNewPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_new_password)
        val etConfirmPassword = dialogView.findViewById<com.google.android.material.textfield.TextInputEditText>(R.id.et_confirm_password)
        
        androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Change Password")
            .setView(dialogView)
            .setPositiveButton("Change") { _, _ ->
                val currentPassword = etCurrentPassword.text.toString()
                val newPassword = etNewPassword.text.toString()
                val confirmPassword = etConfirmPassword.text.toString()
                
                if (validatePasswordChange(currentPassword, newPassword, confirmPassword)) {
                    // In a real app, you would change the password here
                    Toast.makeText(this, "Password changed successfully", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
    
    private fun validatePasswordChange(currentPassword: String, newPassword: String, confirmPassword: String): Boolean {
        if (currentPassword.isEmpty()) {
            Toast.makeText(this, "Current password is required", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (newPassword.length < 6) {
            Toast.makeText(this, "New password must be at least 6 characters", Toast.LENGTH_SHORT).show()
            return false
        }
        
        if (newPassword != confirmPassword) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
            return false
        }
        
        return true
    }
    
    private fun toggleEditMode(isEditMode: Boolean) {
        binding.apply {
            etFullName.isEnabled = isEditMode
            etEmail.isEnabled = isEditMode
            etPhone.isEnabled = isEditMode
            etBio.isEnabled = isEditMode
            
            btnEditProfile.visibility = if (isEditMode) android.view.View.GONE else android.view.View.VISIBLE
            btnSaveProfile.visibility = if (isEditMode) android.view.View.VISIBLE else android.view.View.GONE
            btnCancelEdit.visibility = if (isEditMode) android.view.View.VISIBLE else android.view.View.GONE
        }
    }
    
    // Helper methods for storing additional profile data
    private fun getStoredPhoneNumber(): String {
        return getSharedPreferences("profile_prefs", MODE_PRIVATE)
            .getString("phone_number", "") ?: ""
    }
    
    private fun saveStoredPhoneNumber(phone: String) {
        getSharedPreferences("profile_prefs", MODE_PRIVATE)
            .edit()
            .putString("phone_number", phone)
            .apply()
    }
    
    private fun getStoredBio(): String {
        return getSharedPreferences("profile_prefs", MODE_PRIVATE)
            .getString("bio", "") ?: ""
    }
    
    private fun saveStoredBio(bio: String) {
        getSharedPreferences("profile_prefs", MODE_PRIVATE)
            .edit()
            .putString("bio", bio)
            .apply()
    }
    
    private fun getStoredProfileImage(): String {
        return getSharedPreferences("profile_prefs", MODE_PRIVATE)
            .getString("profile_image", "") ?: ""
    }
    
    private fun saveProfileImage(imageUri: String) {
        getSharedPreferences("profile_prefs", MODE_PRIVATE)
            .edit()
            .putString("profile_image", imageUri)
            .apply()
    }
    
    private fun loadProfileImage() {
        val imageUri = getStoredProfileImage()
        if (imageUri.isNotEmpty()) {
            try {
                binding.ivProfileAvatar.setImageURI(Uri.parse(imageUri))
            } catch (e: Exception) {
                binding.ivProfileAvatar.setImageResource(R.drawable.ic_person)
            }
        } else {
            binding.ivProfileAvatar.setImageResource(R.drawable.ic_person)
        }
    }
}
