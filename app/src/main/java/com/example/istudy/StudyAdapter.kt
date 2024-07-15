package com.example.istudy

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView


class StudyAdapter(
    private val topics: List<TopicModel>,
    private val listener: OnItemClickListener
// Item click listener so users can click with the items of the list
) : RecyclerView.Adapter<StudyAdapter.TopicViewHolder>() {

    private val colors = listOf(
        Color.parseColor("#FFEBEE"), // Light Red
        Color.parseColor("#FFF3E0"), // Light Orange
        Color.parseColor("#FFFDE7"), // Light Yellow
        Color.parseColor("#E8F5E9"), // Light Green
        Color.parseColor("#E3F2FD"), // Light Blue
        Color.parseColor("#F3E5F5")  // Light Purple
    )

    inner class TopicViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {
        val topicNameTextView: TextView = itemView.findViewById(R.id.topicNameTextView)
        val topicCourseTextView: TextView = itemView.findViewById(R.id.topicCourseTextView)

        // Call onclick method
        init {
            itemView.setOnClickListener(this)
        }

        // Function when item view is clicked
        override fun onClick(v: View?) {
            val position = adapterPosition // Gets the position of the item in the list
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(topics[position]) // Calls on item click function onitemclick listener
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.activity_study_adapter, parent, false)
        return TopicViewHolder(itemView)
    }
    // Create a new view holder to every items on the list

    // Bind data to the view holder
    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currentItem = topics[position] // Gets the topicmodel from a specified position

        // Holders are like setters
        holder.topicNameTextView.text = currentItem.topicName
        holder.topicCourseTextView.text = currentItem.topicCourse
        val backgroundColor = colors[position % colors.size]
        holder.itemView.setBackgroundColor(backgroundColor)
    }

    override fun getItemCount() = topics.size

    // Response for item clicks
    interface OnItemClickListener {
        fun onItemClick(topic: TopicModel)
    }

}
