package com.example.quiz.domain.questions

import com.example.quiz.domain.Answer
import com.example.quiz.domain.QuestionTheme

data class MultipleChoice(
    override val id: String,
    override val theme: QuestionTheme,
    override val difficultyLevel: Int,
    override val description: String,
    val correctAnswer: Int,
    val answerOptions: List<String>

): QuestionType{

    init {
        require(description.isNotEmpty()){ "Описане должно быть не пустым!" }
        require(answerOptions.size > 1){ "В задании <<$description>> меньше 2 ответов!"}
        require(correctAnswer in 1..answerOptions.size){
            "В задании <<$description>> correctAnswer = $correctAnswer, а вариантов всего ${answerOptions.size}" }
        requireValidDifficulty(difficultyLevel, description)
    }

    override fun checkCorrect(answer: Answer): Boolean {
        val numberOfOptions = answerOptions.size
        require(answer is Answer.Choice) {"Для <<$description>> нужен ответ типа Choice"}

        require(answer.index in 1..numberOfOptions) {"Ответ должен быть вариантом из списка!"}

        return answer.index == correctAnswer
    }

}