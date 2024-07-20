package com.example.istudy

import android.animation.ValueAnimator
import android.content.Intent
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
    private val totalQuestions: Int get() = questions.size
    private var correctAnswerPosition = -1
    private var score = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

        score = 0

        val topicId = requireActivity().intent.getLongExtra("TOPIC_ID", -1)

        dbHelper = DBHelper(requireContext())

        questions = dbHelper.getQuestions(topicId).shuffled() // Shuffle questions for random order

        if (questions.isNotEmpty()) {
            displayQuestion(0)
        } else {
            binding.questionTextView.text = "No questions available for this topic."
        }

        binding.choice1Button.setOnClickListener { checkAnswer(1) }
        binding.choice2Button.setOnClickListener { checkAnswer(2) }
        binding.choice3Button.setOnClickListener { checkAnswer(3) }
        binding.choice4Button.setOnClickListener { checkAnswer(4) }
        binding.backButton.setOnClickListener { navigateBackToMain() }

        savedInstanceState?.let {
            if (it.getBoolean("RESTART_QUIZ", false)) {
                resetQuiz()
            }
        }
    }

    private fun resetQuiz() {
        // Reset quiz state
        score = 0
        currentQuestionIndex = 0
        // Load questions again if needed
        val topicId = requireActivity().intent.getLongExtra("TOPIC_ID", -1)
        dbHelper = DBHelper(requireContext())
        questions = dbHelper.getQuestions(topicId).shuffled()
        if (questions.isNotEmpty()) {
            displayQuestion(0)
        } else {
            binding.questionTextView.text = "No questions available for this topic."
        }
    }

    private fun displayQuestion(index: Int) {
        val question = questions[index]
        binding.questionTextView.text = question.question

        val shuffledChoices = question.getShuffledChoices()
        binding.choice1Button.text = shuffledChoices[0]
        binding.choice2Button.text = shuffledChoices[1]
        binding.choice3Button.text = shuffledChoices[2]
        binding.choice4Button.text = shuffledChoices[3]

        // Store the correct answer's position for later checking
        correctAnswerPosition = shuffledChoices.indexOf(question.answer)

        // Update progress bar
        updateProgressBar()
    }

    private fun checkAnswer(selectedChoice: Int) {
        val selectedAnswerPosition = when (selectedChoice) {
            1 -> 0
            2 -> 1
            3 -> 2
            4 -> 3
            else -> -1
        }

        if (selectedAnswerPosition == correctAnswerPosition) {
            // For Correct Answer
            score++
        } else {
            // For Wrong Answer
        }

        // Go to next question or if done
        currentQuestionIndex++
        if (currentQuestionIndex < questions.size) {
            displayQuestion(currentQuestionIndex)
        } else {
            // After all questions are done
            showScore()
        }
    }

    private fun updateProgressBar() {
        val progress = ((currentQuestionIndex + 1).toFloat() / totalQuestions.toFloat()) * 100

        // Animate progress bar
        val animator = ValueAnimator.ofInt(binding.progressBar.progress, progress.toInt())
        animator.duration = 500 // Duration in milliseconds
        animator.addUpdateListener { animation ->
            binding.progressBar.progress = animation.animatedValue as Int
        }
        animator.start()
    }

    private fun navigateBackToMain() {
        startActivity(Intent(requireContext(), MainActivity::class.java))
        requireActivity().finish() // Optional: Finish the current activity if you want to close it
    }

    private fun showScore() {
        val scoreFragment = ScoreFragment()
        val bundle = Bundle().apply {
            putInt("SCORE", score)
            putInt("TOTAL_QUESTIONS", totalQuestions)
            putBoolean("RESTART_QUIZ", true) // Flag to indicate quiz restart
        }
        scoreFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainerView2, scoreFragment)
            .addToBackStack(null)
            .commit()
    }
}