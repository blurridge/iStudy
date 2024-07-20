package com.example.istudy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.istudy.databinding.FragmentEditProfileBinding
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream

class EditProfileFragment : Fragment() {

    private var _binding: FragmentEditProfileBinding? = null
    private val binding get() = _binding!!
    private var newProfileImagePath: String? = null

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
            newProfileImagePath?.let { path ->
                profileViewModel.setProfileImagePath(path)
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
            result.data?.data?.let { uri ->
                val bitmap = MediaStore.Images.Media.getBitmap(requireContext().contentResolver, uri)
                val filePath = saveImageToInternalStorage(bitmap)
                filePath?.let { path ->
                    newProfileImagePath = path
                    binding.editProfileImageView.setImageBitmap(bitmap)
                }
            }
        }
    }

    private fun saveImageToInternalStorage(bitmap: Bitmap): String? {
        val context = requireContext()
        val directory = context.getDir("profile_images", Context.MODE_PRIVATE)
        val file = File(directory, "profile_image.png")

        return try {
            val outputStream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.flush()
            outputStream.close()
            file.absolutePath
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
