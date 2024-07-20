package com.example.istudy

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.istudy.databinding.FragmentQuizResultBinding

class ScoreFragment : Fragment() {

    private var _binding: FragmentQuizResultBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentQuizResultBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Retrieve score and total questions from arguments
        val score = arguments?.getInt("SCORE") ?: 0
        val totalQuestions = arguments?.getInt("TOTAL_QUESTIONS") ?: 0

        // Retrieve user name from SharedPreferences
        val sharedPreferences = requireContext().getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)
        val userName = sharedPreferences.getString("USER_NAME", "User") ?: "User"

        // Set score and user name
        binding.scoreTextView.text = "$score/$totalQuestions"
        if(score>=totalQuestions/2){
            binding.parentLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.green_gradient)
            binding.resultDescription.text = "Congratulations $userName, You got ${(score.toFloat() / totalQuestions * 100).toInt()}% of the questions right!"
        }
        else {
            binding.parentLayout.background = ContextCompat.getDrawable(requireContext(), R.drawable.red_gradient)
            binding.resultDescription.text = "You can do better $userName, You got ${(score.toFloat() / totalQuestions * 100).toInt()}% of the questions right!"
        }
        binding.tryAgainButton.setOnClickListener {
            // Restart the quiz by navigating back to QuizFragment
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainerView2, QuizFragment())
                .addToBackStack(null)
                .commit()
        }
        binding.exitButton.setOnClickListener {
            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
