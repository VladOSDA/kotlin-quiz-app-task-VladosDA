package com.example.quiz.domain

class Attempt(
    private val questions: List<QuestionType>,
    val questionLimit: Int
) {

    init {
        require(questionLimit > 0) { "Предел вопросов должен быть выше 0!" }
        require(questions.isNotEmpty()) { "Список вопросов не может быть пустым!" }
        require(questions.distinctBy { it.id }.size == questions.size) {
            "Id вопросов должны быть уникальны!"
        }
    }

    private val lowestLevel = questions.minOf { it.difficultyLevel }
    private val highestLevel = questions.maxOf { it.difficultyLevel }

    private val answeredQuestions = mutableListOf<AnsweredQuestion>()

    var currentDifficultyLevel: Int = lowestLevel
        private set

    private var currentQuestionId: String? =
        questions.first { it.difficultyLevel == lowestLevel }.id

    var isFinished: Boolean = false
        private set

    var timeout: Boolean = false
        set(value) {
            field = field || value
            if (field) finish()                                  // правило 6
        }

    val total: Int get() = questions.size

    val answeredCount: Int get() = answeredQuestions.size

    val score: Int get() = answeredQuestions.count { it.isCorrect }

    val answers: List<AnsweredQuestion> get() = answeredQuestions

    val currentQuestion: QuestionType?
        get() = currentQuestionId?.let { id -> questions.firstOrNull { it.id == id } }

    /** Всего ступеней в теме. */
    val ladderSize: Int get() = highestLevel - lowestLevel + 1

    /** Текущая ступень, считая от 1. */
    val ladderStep: Int get() = currentDifficultyLevel - lowestLevel + 1



    fun addAnswer(userAnswer: Answer): Boolean {
        check(!isFinished) { "Попытка уже завершена" }
        val question = currentQuestion
            ?: throw IllegalStateException("Нет текущего вопроса")

        val correct = question.checkCorrect(userAnswer)
        answeredQuestions += AnsweredQuestion(question.id, userAnswer, correct)


        if (correct && question.difficultyLevel >= highestLevel) {
            finish()
            return correct
        }

        if (answeredCount >= questionLimit) {
            finish()
            return correct
        }

        val targetLevel = if (correct) {
            (currentDifficultyLevel + 1).coerceAtMost(highestLevel)
        } else {
            (currentDifficultyLevel - 1).coerceAtLeast(lowestLevel)
        }

        val next = nextQuestion(targetLevel) ?: nextQuestion(currentDifficultyLevel)

        if (next == null) {
            finish()
            return correct
        }

        currentQuestionId = next.id
        currentDifficultyLevel = next.difficultyLevel
        return correct
    }

    private fun nextQuestion(level: Int): QuestionType? =
        questions.firstOrNull { question -> question.difficultyLevel == level && question.id !in answeredQuestions.map{it.questionId} }

    private fun finish() {
        isFinished = true
        currentQuestionId = null
    }
}