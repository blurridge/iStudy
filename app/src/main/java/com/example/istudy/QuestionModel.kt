package com.example.istudy

class QuestionModel(
    val questionId: Long = 0,
    val topicId: Long,
    val question: String,
    val answer: String,
    val choice1: String,
    val choice2: String,
    val choice3: String,
    val choice4: String
) {
    fun getShuffledChoices(): List<String> {
        val choices = listOf(choice1, choice2, choice3, choice4)
        return choices.shuffled()
    }
}