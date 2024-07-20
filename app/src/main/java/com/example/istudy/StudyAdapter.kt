package com.example.istudy

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView

class StudyAdapter(
    private var topics: MutableList<TopicModel>,  // Changed from List to MutableList
    private val context: Context
) : RecyclerView.Adapter<StudyAdapter.TopicViewHolder>() {

    private val dbHelper = DBHelper(context) // Initialize DBHelper
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

    @SuppressLint("ClickableViewAccessibility")
    override fun onBindViewHolder(holder: TopicViewHolder, position: Int) {
        val currentItem = topics[position]

        holder.topicNameTextView.text = currentItem.topicName
        holder.topicCourseTextView.text = currentItem.topicCourse

        val cardView = holder.itemView as MaterialCardView
        val backgroundColor = colors[position % colors.size]
        cardView.setCardBackgroundColor(backgroundColor)

        holder.takeQuizButton.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, TakeQuizActivity::class.java).apply {
                putExtra("TOPIC_ID", currentItem.topicId)
            }
            context.startActivity(intent)
        }

        // Long press to delete the card
        val handler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            removeTopicAt(position)
        }

        holder.itemView.setOnLongClickListener {
            handler.postDelayed(runnable, 3000) // 3 seconds delay
            true
        }

        holder.itemView.setOnTouchListener { _, event ->
            handler.removeCallbacks(runnable)
            false
        }
    }

    override fun getItemCount() = topics.size

    fun updateTopics(newTopics: List<TopicModel>) {
        topics.clear()
        topics.addAll(newTopics)
        notifyDataSetChanged()
    }

    private fun removeTopicAt(position: Int) {
        val topicId = topics[position].topicId
        if (dbHelper.deleteTopic(topicId)) { // Remove from database
            topics.removeAt(position)
            notifyItemRemoved(position)
        } else {
            Log.e("StudyAdapter", "Failed to delete topic from database")
        }
    }
}