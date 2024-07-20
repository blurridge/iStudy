package com.example.istudy

import FlashcardResponse
import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteConstraintException
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.istudy.DBSchema.DATABASE_NAME
import com.example.istudy.DBSchema.DATABASE_VERSION
import com.example.istudy.DBSchema.TopicEntity
import com.example.istudy.DBSchema.QuestionEntity
import kotlinx.serialization.json.Json

class DBHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        val createTopicsTable = """
            CREATE TABLE ${TopicEntity.TABLE_TOPICS} (
                ${TopicEntity.COLUMN_TOPIC_ID} INTEGER PRIMARY KEY AUTOINCREMENT,
                ${TopicEntity.COLUMN_TOPIC_NAME} TEXT NOT NULL,
                ${TopicEntity.COLUMN_TOPIC_COURSE} TEXT NOT NULL
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

        insertDummyData(db)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS ${QuestionEntity.TABLE_QUESTIONS}")
        db.execSQL("DROP TABLE IF EXISTS ${TopicEntity.TABLE_TOPICS}")
        onCreate(db)
    }

    @Throws(SQLiteConstraintException::class)
    private fun insertTopic(db: SQLiteDatabase, topic: TopicModel): Long {
        val values = ContentValues().apply {
            put(TopicEntity.COLUMN_TOPIC_NAME, topic.topicName)
            put(TopicEntity.COLUMN_TOPIC_COURSE, topic.topicCourse)
        }
        return db.insert(TopicEntity.TABLE_TOPICS, null, values)
    }

    @Throws(SQLiteConstraintException::class)
    private fun insertQuestion(db: SQLiteDatabase, question: QuestionModel): Boolean {
        val values = ContentValues().apply {
            put(QuestionEntity.COLUMN_QUESTION_TEXT, question.question)
            put(QuestionEntity.COLUMN_ANSWER, question.answer)
            put(QuestionEntity.COLUMN_TOPIC_ID, question.topicId)
            put(QuestionEntity.COLUMN_CHOICE_1, question.choice1)
            put(QuestionEntity.COLUMN_CHOICE_2, question.choice2)
            put(QuestionEntity.COLUMN_CHOICE_3, question.choice3)
            put(QuestionEntity.COLUMN_CHOICE_4, question.choice4)
        }
        val success = db.insert(QuestionEntity.TABLE_QUESTIONS, null, values)
        return success != -1L
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteTopic(topicId: Long): Boolean {
        val db = writableDatabase
        val success = db.delete(TopicEntity.TABLE_TOPICS, "${TopicEntity.COLUMN_TOPIC_ID}=?", arrayOf(topicId.toString()))
        db.close()
        return success != 0
    }

    @Throws(SQLiteConstraintException::class)
    fun deleteQuestion(questionId: Long): Boolean {
        val db = writableDatabase
        val success = db.delete(QuestionEntity.TABLE_QUESTIONS, "${QuestionEntity.COLUMN_QUESTION_ID}=?", arrayOf(questionId.toString()))
        db.close()
        return success != 0
    }

    @Throws(SQLiteConstraintException::class)
    fun getTopics(): List<TopicModel> {
        val db = readableDatabase
        val topics = mutableListOf<TopicModel>()
        val cursor: Cursor = db.query(
            TopicEntity.TABLE_TOPICS,
            arrayOf(TopicEntity.COLUMN_TOPIC_ID, TopicEntity.COLUMN_TOPIC_NAME, TopicEntity.COLUMN_TOPIC_COURSE),
            null, null, null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                val topicId = getLong(getColumnIndexOrThrow(TopicEntity.COLUMN_TOPIC_ID))
                val topicName = getString(getColumnIndexOrThrow(TopicEntity.COLUMN_TOPIC_NAME))
                val topicCourse = getString(getColumnIndexOrThrow(TopicEntity.COLUMN_TOPIC_COURSE))
                topics.add(TopicModel(topicId, topicName, topicCourse))
            }
            close()
        }
        db.close()
        return topics
    }

    @Throws(SQLiteConstraintException::class)
    fun getQuestions(topicId: Long): List<QuestionModel> {
        val db = readableDatabase
        val questions = mutableListOf<QuestionModel>()
        val cursor: Cursor = db.query(
            QuestionEntity.TABLE_QUESTIONS,
            null,
            "${QuestionEntity.COLUMN_TOPIC_ID}=?",
            arrayOf(topicId.toString()),
            null, null, null
        )

        with(cursor) {
            while (moveToNext()) {
                val questionId = getLong(getColumnIndexOrThrow(QuestionEntity.COLUMN_QUESTION_ID))
                val questionText = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_QUESTION_TEXT))
                val answer = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_ANSWER))
                val choice1 = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_CHOICE_1))
                val choice2 = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_CHOICE_2))
                val choice3 = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_CHOICE_3))
                val choice4 = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_CHOICE_4))
                questions.add(
                    QuestionModel(
                        questionId,
                        topicId,
                        questionText,
                        answer,
                        choice1,
                        choice2,
                        choice3,
                        choice4
                    )
                )
            }
            close()
        }
        db.close()
        return questions
    }

    @Throws(SQLiteConstraintException::class)
    fun insertFlashcards(responseText: String) {
        val flashcardResponse = Json.decodeFromString<FlashcardResponse>(responseText)
        val db = writableDatabase

        // Insert the topic and retrieve the generated topicId
        val topic = TopicModel(
            topicName = flashcardResponse.topic_name,
            topicCourse = flashcardResponse.course
        )
        val topicId = insertTopic(db, topic)

        // Insert the questions
        flashcardResponse.questions.forEach { question ->
            val questionModel = QuestionModel(
                question = question.question,
                answer = question.answer,
                topicId = topicId,
                choice1 = question.choice1,
                choice2 = question.choice2,
                choice3 = question.choice3,
                choice4 = question.choice4
            )
            insertQuestion(db, questionModel)
        }

        db.close()
    }

    private fun insertDummyData(db: SQLiteDatabase) {
        val topics = listOf(
            TopicModel(topicId = 1, topicName = "Science", topicCourse = "Physics"),
            TopicModel(topicId = 2, topicName = "Math", topicCourse = "Algebra"),
            TopicModel(topicId = 3, topicName = "History", topicCourse = "World History"),
        )

        topics.forEach { topic ->
            insertTopic(db, topic)
        }

        val questions = listOf(
            // Science Questions
            QuestionModel(questionId = 1, topicId = 1, question = "What is the speed of light?", answer = "299,792 km/s", choice1 = "150,000 km/s", choice2 = "299,792 km/s", choice3 = "300,000 km/s", choice4 = "100,000 km/s"),
            QuestionModel(questionId = 2, topicId = 1, question = "What is the chemical symbol for water?", answer = "H2O", choice1 = "H2", choice2 = "O2", choice3 = "H2O", choice4 = "CO2"),
            QuestionModel(questionId = 3, topicId = 1, question = "What planet is known as the Red Planet?", answer = "Mars", choice1 = "Earth", choice2 = "Venus", choice3 = "Mars", choice4 = "Jupiter"),
            QuestionModel(questionId = 4, topicId = 1, question = "What is the powerhouse of the cell?", answer = "Mitochondria", choice1 = "Nucleus", choice2 = "Ribosome", choice3 = "Mitochondria", choice4 = "Chloroplast"),
            QuestionModel(questionId = 5, topicId = 1, question = "What force keeps us on the ground?", answer = "Gravity", choice1 = "Magnetism", choice2 = "Electrostatic", choice3 = "Gravity", choice4 = "Friction"),
            QuestionModel(questionId = 6, topicId = 1, question = "What gas do plants absorb from the atmosphere?", answer = "Carbon dioxide", choice1 = "Oxygen", choice2 = "Nitrogen", choice3 = "Carbon dioxide", choice4 = "Hydrogen"),
            QuestionModel(questionId = 7, topicId = 1, question = "What is the boiling point of water?", answer = "100°C", choice1 = "0°C", choice2 = "50°C", choice3 = "100°C", choice4 = "200°C"),
            QuestionModel(questionId = 8, topicId = 1, question = "What is the primary gas found in the sun?", answer = "Hydrogen", choice1 = "Oxygen", choice2 = "Nitrogen", choice3 = "Hydrogen", choice4 = "Helium"),
            QuestionModel(questionId = 9, topicId = 1, question = "What is the hardest natural substance on Earth?", answer = "Diamond", choice1 = "Gold", choice2 = "Iron", choice3 = "Diamond", choice4 = "Quartz"),
            QuestionModel(questionId = 10, topicId = 1, question = "What is the most abundant gas in the Earth's atmosphere?", answer = "Nitrogen", choice1 = "Oxygen", choice2 = "Carbon dioxide", choice3 = "Nitrogen", choice4 = "Argon"),

            // Math Questions
            QuestionModel(questionId = 11, topicId = 2, question = "What is 2 + 2?", answer = "4", choice1 = "3", choice2 = "4", choice3 = "5", choice4 = "6"),
            QuestionModel(questionId = 12, topicId = 2, question = "What is the square root of 16?", answer = "4", choice1 = "2", choice2 = "4", choice3 = "8", choice4 = "10"),
            QuestionModel(questionId = 13, topicId = 2, question = "What is the value of π (pi)?", answer = "3.14", choice1 = "2.17", choice2 = "3.14", choice3 = "1.41", choice4 = "2.71"),
            QuestionModel(questionId = 14, topicId = 2, question = "What is 5 * 6?", answer = "30", choice1 = "20", choice2 = "30", choice3 = "25", choice4 = "36"),
            QuestionModel(questionId = 15, topicId = 2, question = "What is 9 / 3?", answer = "3", choice1 = "6", choice2 = "3", choice3 = "9", choice4 = "12"),
            QuestionModel(questionId = 16, topicId = 2, question = "What is the perimeter of a rectangle with sides 3 and 4?", answer = "14", choice1 = "7", choice2 = "14", choice3 = "12", choice4 = "10"),
            QuestionModel(questionId = 17, topicId = 2, question = "What is the area of a circle with radius 1?", answer = "π", choice1 = "π", choice2 = "2π", choice3 = "π²", choice4 = "2π²"),
            QuestionModel(questionId = 18, topicId = 2, question = "What is 10 - 3?", answer = "7", choice1 = "7", choice2 = "6", choice3 = "8", choice4 = "5"),
            QuestionModel(questionId = 19, topicId = 2, question = "What is the value of the Golden Ratio (φ)?", answer = "1.618", choice1 = "1.618", choice2 = "2.718", choice3 = "3.142", choice4 = "0.577"),
            QuestionModel(questionId = 20, topicId = 2, question = "What is the sum of angles in a triangle?", answer = "180°", choice1 = "180°", choice2 = "90°", choice3 = "360°", choice4 = "270°"),

            // History Questions
            QuestionModel(questionId = 21, topicId = 3, question = "Who was the first president of the United States?", answer = "George Washington", choice1 = "Abraham Lincoln", choice2 = "George Washington", choice3 = "Thomas Jefferson", choice4 = "John Adams"),
            QuestionModel(questionId = 22, topicId = 3, question = "What year did World War II end?", answer = "1945", choice1 = "1939", choice2 = "1941", choice3 = "1945", choice4 = "1949"),
            QuestionModel(questionId = 23, topicId = 3, question = "Who was known as the Maid of Orléans?", answer = "Joan of Arc", choice1 = "Marie Curie", choice2 = "Joan of Arc", choice3 = "Eleanor of Aquitaine", choice4 = "Catherine de' Medici"),
            QuestionModel(questionId = 24, topicId = 3, question = "What ancient civilization built the pyramids?", answer = "Egyptians", choice1 = "Mayans", choice2 = "Egyptians", choice3 = "Romans", choice4 = "Greeks"),
            QuestionModel(questionId = 25, topicId = 3, question = "Who wrote the 'Iliad' and the 'Odyssey'?", answer = "Homer", choice1 = "Plato", choice2 = "Aristotle", choice3 = "Homer", choice4 = "Sophocles"),
            QuestionModel(questionId = 26, topicId = 3, question = "Who was the British prime minister during World War II?", answer = "Winston Churchill", choice1 = "Neville Chamberlain", choice2 = "Winston Churchill", choice3 = "Clement Attlee", choice4 = "Margaret Thatcher"),
            QuestionModel(questionId = 27, topicId = 3, question = "What event started World War I?", answer = "Assassination of Archduke Franz Ferdinand", choice1 = "Sinking of the Lusitania", choice2 = "Zimmermann Telegram", choice3 = "Assassination of Archduke Franz Ferdinand", choice4 = "Treaty of Versailles"),
            QuestionModel(questionId = 28, topicId = 3, question = "Who discovered penicillin?", answer = "Alexander Fleming", choice1 = "Louis Pasteur", choice2 = "Alexander Fleming", choice3 = "Marie Curie", choice4 = "Gregor Mendel"),
            QuestionModel(questionId = 29, topicId = 3, question = "What was the name of the ship that brought the Pilgrims to America?", answer = "Mayflower", choice1 = "Santa Maria", choice2 = "Mayflower", choice3 = "Beagle", choice4 = "Endeavour"),
            QuestionModel(questionId = 30, topicId = 3, question = "What was the name of the first manned mission to land on the moon?", answer = "Apollo 11", choice1 = "Gemini 8", choice2 = "Apollo 11", choice3 = "Apollo 13", choice4 = "Apollo 7")
        )

        questions.forEach { question ->
            insertQuestion(db, question)
        }
    }
}
