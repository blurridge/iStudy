package com.example.istudy

import android.content.Intent
import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class StudyAdapter(
    private val topics: List<TopicModel>
) : RecyclerView.Adapter<StudyAdapter.TopicViewHolder>() {

    private val colors = listOf(
        Color.parseColor("#FFEBEE"), // Light Red
        Color.parseColor("#FFF3E0"), // Light Orange
        Color.parseColor("#FFFDE7"), // Light Yellow
        Color.parseColor("#E8F5E9"), // Light Green
        Color.parseColor("#E3F2FD"), // Light Blue
        Color.parseColor("#F3E5F5")  // Light Purple
    )

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val topicNameTextView: TextView = itemView.findViewById(R.id.topicNameTextView)
        val topicCourseTextView: TextView = itemView.findViewById(R.id.topicCourseTextView)
        val takeQuizButton: Button = itemView.findViewById(R.id.takeQuizButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_study_adapter, parent, false)
        return TopicViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currentItem = topics[position] // Gets the TopicModel from a specified position

        // Holders are like setters
        holder.topicNameTextView.text = currentItem.topicName
        holder.topicCourseTextView.text = currentItem.topicCourse

        // Iterate through the color list so it will have different colors
        val backgroundColor = colors[position % colors.size]
        holder.itemView.setBackgroundColor(backgroundColor)

        // Button for taking quiz
        holder.takeQuizButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TakeQuizActivity::class.java).apply {
                putExtra("TOPIC_ID", currentItem.topicId)
            }
            context.startActivity(intent)
        }
    }

    override fun getItemCount() = topics.size
}
