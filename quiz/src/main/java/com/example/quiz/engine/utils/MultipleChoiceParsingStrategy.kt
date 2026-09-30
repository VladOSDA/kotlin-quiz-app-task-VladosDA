package com.example.quiz.engine.utils

import com.example.quiz.domain.Answer
import com.example.quiz.domain.questions.MultipleChoice
import com.example.quiz.domain.questions.QuestionType

class MultipleChoiceParsingStrategy : AnswerParsingStrategy {
    override fun parseUserInput(question: QuestionType, raw: String): Answer {
        require(question is MultipleChoice) {"Вопрос должен быть multiple choice"}

        val number = raw.toIntOrNull()
            ?: throw IllegalArgumentException(
                "Введите номер варианта: 1..${question.answerOptions.size}"
            )
        if (number !in 1..question.answerOptions.size) {
            throw IllegalArgumentException(
                "Варианта «$number» нет — доступны номера 1..${question.answerOptions.size}"
            )
        }
        return Answer.Choice(number)
    }
}