package com.example.istudy

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.istudy.databinding.FragmentStudyBinding

class StudyFragment : Fragment() {
    private lateinit var binding: FragmentStudyBinding
    private lateinit var dbHelper: DBHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize DBHelper using  fragment's context
        dbHelper = DBHelper(requireContext())

        // Get topic function from db helper
        val topics = dbHelper.getTopics()


        if (topics.isEmpty()) {
            Toast.makeText(requireContext(), "No topics available", Toast.LENGTH_SHORT).show()
        } else {
            // Create instance of the adapter and pass the topics for display
            val adapter = StudyAdapter(topics)
            binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())

            // Sets the custom adapter made for the recycler view
            binding.recyclerView.adapter = adapter
        }
    }
}
