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
    private lateinit var adapter: StudyAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStudyBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initialize DBHelper using fragment's context
        dbHelper = DBHelper(requireContext())

        // Get topic function from db helper
        val topics = dbHelper.getTopics()

        // Initialize the adapter with the list of topics (which might be empty)
        adapter = StudyAdapter(topics.toMutableList(), requireContext())
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter

        if (topics.isEmpty()) {
            Toast.makeText(requireContext(), "No topics available", Toast.LENGTH_SHORT).show()
        }
    }

    fun refreshData() {
        val newTopics = dbHelper.getTopics()
        adapter.updateTopics(newTopics)
    }
}