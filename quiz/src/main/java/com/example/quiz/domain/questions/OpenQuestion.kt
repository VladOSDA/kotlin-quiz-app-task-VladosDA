package com.example.quiz.domain.questions

import com.example.quiz.domain.Answer
import com.example.quiz.domain.QuestionTheme

data class OpenQuestion(
    override val id: String,
    override val theme: QuestionTheme,
    override val difficultyLevel: Int,
    override val description: String,
    val correctAnswer: String

): QuestionType{

    init {
        require(description.isNotEmpty()){ "Описане должно быть не пустым!" }
        require(correctAnswer.isNotEmpty()){
            "В задании <<$description>> пустой правильный ответ!" }
        QuestionType.requireValidDifficulty(difficultyLevel, description)
    }

    override fun checkCorrect(answer: Answer): Boolean {

        require(answer is Answer.Text){"Для <<$description>> нужен ответ типа Text"}
        require (answer.value.isNotEmpty()) {"Ответ должен быть не пустым!"}
        require(answer.value.trim().isNotEmpty()) {"Ответ должен состоять не только из пробелов!"}
        return (answer.value.trim().equals(correctAnswer.trim(), ignoreCase = true))
    }

}