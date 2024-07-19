package com.example.istudy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.istudy.databinding.FragmentQuizBinding

class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding
    private lateinit var dbHelper: DBHelper
    private lateinit var questions: List<QuestionModel>
    private var currentQuestionIndex = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val topicId = requireActivity().intent.getStringExtra("TOPIC_ID") ?: return

        dbHelper = DBHelper(requireContext())

        questions = dbHelper.getQuestions(topicId.toLong())

        if (questions.isNotEmpty()) {
            displayQuestion(0)
        } else {
            binding.questionTextView.text = "No questions available for this topic."
        }

        binding.choice1Button.setOnClickListener { checkAnswer(1) }
        binding.choice2Button.setOnClickListener { checkAnswer(2) }
        binding.choice3Button.setOnClickListener { checkAnswer(3) }
        binding.choice4Button.setOnClickListener { checkAnswer(4) }
    }

    private fun displayQuestion(index: Int) {
        val question = questions[index]
        binding.questionTextView.text = question.question
        binding.choice1Button.text = question.choice1
        binding.choice2Button.text = question.choice2
        binding.choice3Button.text = question.choice3
        binding.choice4Button.text = question.choice4
    }

    private fun checkAnswer(selectedChoice: Int) {
        val question = questions[currentQuestionIndex]
        val correctAnswer = question.answer

        val selectedAnswer = when (selectedChoice) {
            1 -> question.choice1
            2 -> question.choice2
            3 -> question.choice3
            4 -> question.choice4
            else -> ""
        }

        if (selectedAnswer == correctAnswer) {
            // For Correct Answer
        } else {
            // For Wrong Answer
        }

        // Go to next question or if done
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            displayQuestion(currentQuestionIndex)
        } else {
            // After all questions are done
        }
    }
}
