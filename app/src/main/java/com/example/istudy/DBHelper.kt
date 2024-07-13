package com.example.istudy

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
    private fun insertTopic(db: SQLiteDatabase, topic: TopicModel): Boolean {
        val values = ContentValues().apply {
            put(TopicEntity.COLUMN_TOPIC_ID, topic.topicId)
            put(TopicEntity.COLUMN_TOPIC_NAME, topic.topicName)
            put(TopicEntity.COLUMN_TOPIC_COURSE, topic.topicCourse)
        }
        val success = db.insert(TopicEntity.TABLE_TOPICS, null, values)
        db.close()
        return success != -1L
    }

    @Throws(SQLiteConstraintException::class)
    private fun insertQuestion(db: SQLiteDatabase, question: QuestionModel): Boolean {
        val values = ContentValues().apply {
            put(QuestionEntity.COLUMN_QUESTION_ID, question.questionId)
            put(QuestionEntity.COLUMN_QUESTION_TEXT, question.question)
            put(QuestionEntity.COLUMN_ANSWER, question.answer)
            put(QuestionEntity.COLUMN_TOPIC_ID, question.topicId.toInt())
            put(QuestionEntity.COLUMN_CHOICE_1, question.choice1)
            put(QuestionEntity.COLUMN_CHOICE_2, question.choice2)
            put(QuestionEntity.COLUMN_CHOICE_3, question.choice3)
            put(QuestionEntity.COLUMN_CHOICE_4, question.choice4)
        }
        val success = db.insert(QuestionEntity.TABLE_QUESTIONS, null, values)
        db.close()
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
                val topicId = getString(getColumnIndexOrThrow(TopicEntity.COLUMN_TOPIC_ID))
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
                val questionId = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_QUESTION_ID))
                val questionText = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_QUESTION_TEXT))
                val answer = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_ANSWER))
                val choice1 = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_CHOICE_1))
                val choice2 = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_CHOICE_2))
                val choice3 = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_CHOICE_3))
                val choice4 = getString(getColumnIndexOrThrow(QuestionEntity.COLUMN_CHOICE_4))
                questions.add(
                    QuestionModel(
                        questionId,
                        topicId.toString(),
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

    private fun insertDummyData(db: SQLiteDatabase) {
        val topics = listOf(
            TopicModel("1", "Science", "Physics"),
            TopicModel("2", "Math", "Algebra"),
            TopicModel("3", "History", "World History")
        )

        topics.forEach { topic ->
            insertTopic(db, topic)
        }

        val questions = listOf(
            // Science Questions
            QuestionModel("1", "1", "What is the speed of light?", "299,792 km/s", "150,000 km/s", "299,792 km/s", "300,000 km/s", "100,000 km/s"),
            QuestionModel("2", "1", "What is the chemical symbol for water?", "H2O", "H2", "O2", "H2O", "CO2"),
            QuestionModel("3", "1", "What planet is known as the Red Planet?", "Mars", "Earth", "Venus", "Mars", "Jupiter"),
            QuestionModel("4", "1", "What is the powerhouse of the cell?", "Mitochondria", "Nucleus", "Ribosome", "Mitochondria", "Chloroplast"),
            QuestionModel("5", "1", "What force keeps us on the ground?", "Gravity", "Magnetism", "Electrostatic", "Gravity", "Friction"),
            QuestionModel("6", "1", "What gas do plants absorb from the atmosphere?", "Carbon dioxide", "Oxygen", "Nitrogen", "Carbon dioxide", "Hydrogen"),
            QuestionModel("7", "1", "What is the boiling point of water?", "100°C", "0°C", "50°C", "100°C", "200°C"),
            QuestionModel("8", "1", "What is the primary gas found in the sun?", "Hydrogen", "Oxygen", "Nitrogen", "Hydrogen", "Helium"),
            QuestionModel("9", "1", "What is the hardest natural substance on Earth?", "Diamond", "Gold", "Iron", "Diamond", "Quartz"),
            QuestionModel("10", "1", "What is the most abundant gas in the Earth's atmosphere?", "Nitrogen", "Oxygen", "Carbon dioxide", "Nitrogen", "Argon"),

            // Math Questions
            QuestionModel("11", "2", "What is 2 + 2?", "4", "3", "4", "5", "6"),
            QuestionModel("12", "2", "What is the square root of 16?", "4", "2", "4", "8", "10"),
            QuestionModel("13", "2", "What is the value of pi?", "3.14159", "3", "3.14", "3.14159", "3.2"),
            QuestionModel("14", "2", "What is 5 factorial (5!)?", "120", "60", "120", "24", "720"),
            QuestionModel("15", "2", "What is the solution to the equation x + 3 = 5?", "2", "1", "2", "3", "4"),
            QuestionModel("16", "2", "What is the perimeter of a rectangle with length 5 and width 3?", "16", "8", "15", "16", "10"),
            QuestionModel("17", "2", "What is the area of a triangle with base 4 and height 3?", "6", "7", "6", "12", "9"),
            QuestionModel("18", "2", "What is 10% of 200?", "20", "10", "20", "30", "40"),
            QuestionModel("19", "2", "What is the sum of the angles in a triangle?", "180 degrees", "90 degrees", "180 degrees", "360 degrees", "270 degrees"),
            QuestionModel("20", "2", "What is the derivative of x^2?", "2x", "x", "x^2", "2x", "x^3"),

            // History Questions
            QuestionModel("21", "3", "Who discovered America?", "Christopher Columbus", "Leif Erikson", "Christopher Columbus", "Amerigo Vespucci", "Ferdinand Magellan"),
            QuestionModel("22", "3", "When did World War II end?", "1945", "1918", "1945", "1939", "1965"),
            QuestionModel("23", "3", "Who was the first President of the United States?", "George Washington", "Thomas Jefferson", "George Washington", "Abraham Lincoln", "John Adams"),
            QuestionModel("24", "3", "What ancient civilization built the pyramids?", "Egyptians", "Romans", "Greeks", "Egyptians", "Mayans"),
            QuestionModel("25", "3", "Who was known as the Maid of Orléans?", "Joan of Arc", "Marie Antoinette", "Joan of Arc", "Catherine the Great", "Queen Elizabeth I"),
            QuestionModel("26", "3", "What year did the Titanic sink?", "1912", "1905", "1912", "1918", "1925"),
            QuestionModel("27", "3", "Who was the famous emperor of Rome who was assassinated in 44 BC?", "Julius Caesar", "Augustus", "Nero", "Julius Caesar", "Caligula"),
            QuestionModel("28", "3", "What was the name of the ship that brought the Pilgrims to America?", "Mayflower", "Santa Maria", "Mayflower", "Pinta", "Nina"),
            QuestionModel("29", "3", "What war was fought between the North and South regions in the United States?", "Civil War", "Revolutionary War", "Civil War", "War of 1812", "World War I"),
            QuestionModel("30", "3", "Who was the first woman to fly solo across the Atlantic Ocean?", "Amelia Earhart", "Harriet Quimby", "Bessie Coleman", "Amelia Earhart", "Jacqueline Cochran")
        )

        questions.forEach { question ->
            insertQuestion(db, question)
        }
    }
}
