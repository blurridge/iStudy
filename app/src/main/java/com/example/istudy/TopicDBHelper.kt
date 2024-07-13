package com.example.istudy

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.istudy.DBSchema.DATABASE_NAME
import com.example.istudy.DBSchema.DATABASE_VERSION
import com.example.istudy.DBSchema.TopicEntity
import com.example.istudy.DBSchema.QuestionEntity

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTopicsTable = """
            CREATE TABLE ${TopicEntity.TABLE_TOPICS} (
                ${TopicEntity.COLUMN_TOPIC_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${TopicEntity.COLUMN_TOPIC_NAME} TEXT NOT NULL
            )
        """.trimIndent()

        val createQuestionsTable = """
            CREATE TABLE ${QuestionEntity.TABLE_QUESTIONS} (
                ${QuestionEntity.COLUMN_QUESTION_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${QuestionEntity.COLUMN_QUESTION_TEXT} TEXT NOT NULL,
                ${QuestionEntity.COLUMN_ANSWER} TEXT NOT NULL,
                ${QuestionEntity.COLUMN_TOPIC_ID} INTEGER,
                ${QuestionEntity.COLUMN_CHOICE_1} TEXT NOT NULL,
                ${QuestionEntity.COLUMN_CHOICE_2} TEXT NOT NULL,
                ${QuestionEntity.COLUMN_CHOICE_3} TEXT NOT NULL,
                ${QuestionEntity.COLUMN_CHOICE_4} TEXT NOT NULL,
                FOREIGN KEY(${QuestionEntity.COLUMN_TOPIC_ID}) REFERENCES ${TopicEntity.TABLE_TOPICS}(${TopicEntity.COLUMN_TOPIC_ID})
            )
        """.trimIndent()

        db.execSQL(createTopicsTable)
        db.execSQL(createQuestionsTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${QuestionEntity.TABLE_QUESTIONS}")
        db.execSQL("DROP TABLE IF EXISTS ${TopicEntity.TABLE_TOPICS}")
        onCreate(db)
    }
}
