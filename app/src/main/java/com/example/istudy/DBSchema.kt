package com.example.istudy

import android.provider.BaseColumns

object DBSchema {
    const val DATABASE_VERSION = 1
    const val DATABASE_NAME = "app_database.db"

    class TopicEntity: BaseColumns {
        companion object {
            const val TABLE_TOPICS = "Topics"
            const val COLUMN_TOPIC_ID = "topic_id"
            const val COLUMN_TOPIC_NAME = "topic_name"
        }
    }

    class QuestionEntity: BaseColumns {
        companion object {
            const val TABLE_QUESTIONS = "Questions"
            const val COLUMN_QUESTION_ID = "question_id"
            const val COLUMN_QUESTION_TEXT = "question_text"
            const val COLUMN_ANSWER = "answer"
            const val COLUMN_TOPIC_ID = "topic_id"
            const val COLUMN_CHOICE_1 = "choice_1"
            const val COLUMN_CHOICE_2 = "choice_2"
            const val COLUMN_CHOICE_3 = "choice_3"
            const val COLUMN_CHOICE_4 = "choice_4"
        }
    }
}