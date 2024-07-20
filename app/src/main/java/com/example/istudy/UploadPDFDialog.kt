package com.example.istudy

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.DialogFragment
import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAI
import com.example.istudy.databinding.DialogUploadPdfBinding
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import java.io.InputStream
import kotlin.time.Duration.Companion.seconds

@OptIn(BetaOpenAI::class)
class UploadPdfDialogFragment : DialogFragment() {

    private lateinit var binding: DialogUploadPdfBinding
    private var selectedPdfUri: Uri? = null
    private val OPENAI_API_KEY = "your-api-key"
    private val openai = OpenAI(
        token = OPENAI_API_KEY,
        timeout = Timeout(socket = 60.seconds)
    )

    private val pdfPickerLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult(),
        ActivityResultCallback { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                selectedPdfUri = result.data?.data
                selectedPdfUri?.let {
                    binding.selectedFileText.setText(it.path)
                    Toast.makeText(requireContext(), "PDF selected", Toast.LENGTH_SHORT).show()
                }
            }
        }
    )

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
            pdfPickerLauncher.launch(intent)
        }

        binding.submitButton.setOnClickListener {
            if (selectedPdfUri != null) {
                processPdfAndSubmit(selectedPdfUri!!)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Please select a PDF file first",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun processPdfAndSubmit(uri: Uri) {
        val extractedText = extractTextFromPdf(uri, requireContext())
        Log.d("UploadPdfDialogFragment", "Extracted text: $extractedText")
    }

    private fun extractTextFromPdf(uri: Uri, context: Context): String {
        var extractedText = ""
        try {
            val inputStream: InputStream? = context.contentResolver.openInputStream(uri)
            if (inputStream != null) {
                // Create PdfReader with InputStream
                val pdfReader = PdfReader(inputStream)
                val numberOfPages = pdfReader.numberOfPages

                // Loop through pages and extract text
                for (i in 1..numberOfPages) {
                    extractedText += PdfTextExtractor.getTextFromPage(pdfReader, i).trim() + "\n"
                }

                // Close the PdfReader
                pdfReader.close()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            extractedText = "Error extracting text from PDF: ${e.message}"
        }

        return extractedText
    }
}
