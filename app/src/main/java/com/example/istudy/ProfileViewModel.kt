package com.example.istudy

import android.content.Context
import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.preference.PreferenceManager

class ProfileViewModel : ViewModel() {

    private val _profileName = MutableLiveData<String>()
    val profileName: LiveData<String> get() = _profileName

    private val _profileImageUri = MutableLiveData<Uri>()
    val profileImageUri: LiveData<Uri> get() = _profileImageUri

    private lateinit var context: Context

    fun initialize(context: Context) {
        this.context = context
        loadProfileData()
    }

    private fun loadProfileData() {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        _profileName.value = sharedPreferences.getString("profile_name", "Default Name")
        val profileImageUriString = sharedPreferences.getString("profile_image_uri", null)
        _profileImageUri.value = profileImageUriString?.let { Uri.parse(it) }
    }

    fun setProfileName(name: String) {
        _profileName.value = name
        saveProfileName(name)
    }

    private fun saveProfileName(name: String) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString("profile_name", name)
            apply()
        }
    }

    fun setProfileImageUri(uri: Uri) {
        _profileImageUri.value = uri
        saveProfileImageUri(uri)
    }

    private fun saveProfileImageUri(uri: Uri) {
        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
        with(sharedPreferences.edit()) {
            putString("profile_image_uri", uri.toString())
            apply()
        }
    }
}