package com.example.istudy

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_study_adapter, parent, false)
        return TopicViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currentItem = topics[position] // Gets the TopicModel from a specified position

        // Set the text for TextViews
        holder.topicNameTextView.text = currentItem.topicName
        holder.topicCourseTextView.text = currentItem.topicCourse

        // Cycle through the colors list and apply the background color to the item view
        val backgroundColor = colors[position % colors.size]
        holder.itemView.setBackgroundColor(backgroundColor)
    }

    override fun getItemCount() = topics.size
}
