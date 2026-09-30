package com.example.quiz.engine.utils

import com.example.quiz.domain.Answer
import com.example.quiz.domain.questions.QuestionType

sealed interface AnswerParsingStrategy {
    fun parseUserInput(question: QuestionType, raw: String): Answer
}

