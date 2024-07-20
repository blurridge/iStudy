import kotlinx.serialization.Serializable

@Serializable
data class FlashcardResponse(
    val topic_name: String,
    val course: String,
    val questions: List<Question>
)

@Serializable
data class Question(
    val question: String,
    val answer: String,
    val choice1: String,
    val choice2: String,
    val choice3: String,
    val choice4: String
)
