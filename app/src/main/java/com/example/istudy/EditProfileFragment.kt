package com.example.istudy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.istudy.databinding.FragmentEditProfileBinding

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var newProfileImageUri: Uri? = null

    private val profileViewModel: ProfileViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEditProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve current values from the ViewModel
        profileViewModel.profileName.value?.let { name ->
            binding.editProfileNameEditText.setText(name)
        }

        profileViewModel.profileImageUri.value?.let { uri ->
            binding.editProfileImageView.setImageURI(uri)
        }

        binding.editProfileButton.setOnClickListener {
            pickImageFromGallery()
        }

        binding.saveProfileButton.setOnClickListener {
            val newName = binding.editProfileNameEditText.text.toString()
            profileViewModel.setProfileName(newName)
            newProfileImageUri?.let { uri ->
                profileViewModel.setProfileImageUri(uri)
            }
            parentFragmentManager.popBackStack()
        }
    }

    private fun pickImageFromGallery() {
        val pickImageIntent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(pickImageIntent)
    }

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            newProfileImageUri = result.data?.data
            newProfileImageUri?.let { uri ->
                binding.editProfileImageView.setImageURI(uri)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}