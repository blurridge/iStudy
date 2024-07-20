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
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.message.MessageRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.thread.ThreadId
import com.aallam.openai.api.thread.ThreadMessage
import com.aallam.openai.api.thread.ThreadRequest
import com.aallam.openai.client.OpenAI
import com.example.istudy.databinding.DialogUploadPdfBinding
import com.itextpdf.text.pdf.PdfReader
import com.itextpdf.text.pdf.parser.PdfTextExtractor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.net.InetAddress
import kotlin.time.Duration.Companion.seconds

@OptIn(BetaOpenAI::class)
class UploadPdfDialogFragment : DialogFragment() {

    private lateinit var binding: DialogUploadPdfBinding
    private var selectedPdfUri: Uri? = null
    private val OPENAI_API_KEY = ""
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
        CoroutineScope(Dispatchers.IO).launch {
            withContext(Dispatchers.Main) {
                binding.progressBar.visibility = View.VISIBLE
                binding.selectFileButton.isEnabled = false
                binding.submitButton.isEnabled = false
                binding.closeButton.isEnabled = false
            }

            val extractedText = extractTextFromPdf(uri, requireContext())

            try {
                val chatRequest = ChatCompletionRequest(
                    model = ModelId("gpt-4o-mini"),
                    messages = listOf(
                        ChatMessage(
                            role = Role.System,
                            content = "You are a helpful assistant."
                        ),
                        ChatMessage(
                            role = Role.User,
                            content = """
                        Based on the following notes, create 10 flashcards. Each flashcard should have a question, an answer, and four multiple-choice options. The output should be in the following format. Do not write anything else.:

                        {
                            "topic_name": "Topic derived from notes",
                            "course": "Course derived from notes",
                            "questions": [
                                {
                                    "question": "Generated question based on notes",
                                    "answer": "Correct answer",
                                    "choice1": "Option 1",
                                    "choice2": "Option 2",
                                    "choice3": "Option 3",
                                    "choice4": "Option 4"
                                },
                                // Continue for 9 more questions
                            ]
                        }

                        Here are the notes:
                        $extractedText
                        """.trimIndent()
                        )
                    )
                )

                val response = openai.chatCompletion(
                    request = chatRequest
                )

                val responseText = response.choices.firstOrNull()?.message?.content ?: "No response"
                Log.d("UploadPdfDialogFragment", "Response: $responseText")
                val dbHelper = DBHelper(requireContext())
                dbHelper.insertFlashcards(responseText)

                withContext(Dispatchers.Main) {
                    binding.progressBar.visibility = View.GONE
                    binding.selectFileButton.isEnabled = true
                    binding.submitButton.isEnabled = true
                    binding.closeButton.isEnabled = true
                    Toast.makeText(requireContext(), "Flashcards generated successfully", Toast.LENGTH_SHORT).show()
                    (activity as? MainActivity)?.refreshStudyFragmentData()
                    dismiss()
                }

            } catch (e: Exception) {
                e.printStackTrace()
                withContext(Dispatchers.Main) {
                    Toast.makeText(requireContext(), "Error generating flashcards: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
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
