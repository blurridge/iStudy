    package com.example.istudy

    import android.content.Context
    import android.graphics.BitmapFactory
    import android.net.Uri
    import androidx.lifecycle.LiveData
    import androidx.lifecycle.MutableLiveData
    import androidx.lifecycle.ViewModel
    import androidx.preference.PreferenceManager
    import java.io.File

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
            _profileName.value = sharedPreferences.getString("profile_name", "")
            val profileImagePath = sharedPreferences.getString("profile_image_path", null)
            profileImagePath?.let {
                _profileImageUri.value = Uri.fromFile(File(it))  // Use URI for image loading
            }
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

        fun setProfileImagePath(path: String) {
            _profileImageUri.value = Uri.fromFile(File(path))  // Update LiveData with URI
            saveProfileImagePath(path)
        }

        private fun saveProfileImagePath(path: String) {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context)
            with(sharedPreferences.edit()) {
                putString("profile_image_path", path)
                apply()
            }
        }
    }