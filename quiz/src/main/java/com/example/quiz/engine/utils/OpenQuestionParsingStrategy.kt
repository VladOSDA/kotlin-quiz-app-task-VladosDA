package com.example.quiz.engine.utils

import com.example.quiz.domain.Answer
import com.example.quiz.domain.questions.OpenQuestion
import com.example.quiz.domain.questions.QuestionType

class OpenQuestionParsingStrategy : AnswerParsingStrategy {
    override fun parseUserInput(question: QuestionType, raw: String): Answer {
        require(question is OpenQuestion) {"Вопрос должен быть OpenQuestion"}

        if (raw.isBlank()) throw IllegalArgumentException("Ответ не может быть пустым")
        return Answer.Text(raw)
    }
}