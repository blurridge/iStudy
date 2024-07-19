package com.example.istudy

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.istudy.databinding.DialogUploadPdfBinding

class UploadPdfDialogFragment : DialogFragment() {

    private lateinit var binding: DialogUploadPdfBinding;
    private val PDF_PICK_CODE = 1001
    private var selectedPdfUri: Uri? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogUploadPdfBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.closeButton.setOnClickListener {
            dismiss()
        }

        binding.selectFileButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "application/pdf"
            startActivityForResult(intent, PDF_PICK_CODE)
        }

        binding.submitButton.setOnClickListener {
            if (selectedPdfUri != null) {
                // Handle PDF submission (e.g., upload to server or save locally)
                Toast.makeText(requireContext(), "PDF submitted", Toast.LENGTH_SHORT).show()
                dismiss()
            } else {
                Toast.makeText(requireContext(), "Please select a PDF file first", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PDF_PICK_CODE && resultCode == Activity.RESULT_OK) {
            selectedPdfUri = data?.data
            selectedPdfUri?.let {
                binding.selectedFileText.setText(it.path)
                Toast.makeText(requireContext(), "PDF selected", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
