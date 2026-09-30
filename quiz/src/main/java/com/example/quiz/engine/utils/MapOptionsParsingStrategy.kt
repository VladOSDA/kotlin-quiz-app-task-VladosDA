package com.example.quiz.engine.utils

import com.example.quiz.domain.Answer
import com.example.quiz.domain.questions.MapOptions
import com.example.quiz.domain.questions.QuestionType

class MapOptionsParsingStrategy : AnswerParsingStrategy {
    override fun parseUserInput(question: QuestionType, raw: String): Answer {
        require(question is MapOptions) {"Вопрос должен быть MapOptions"}

        val size = question.list1.size
        val pairs = raw.split(Regex("[,\\s]+"))
            .filter { it.isNotBlank() }
            .map { token ->
                val parts = token.split("-")
                val left = parts.getOrNull(0)?.toIntOrNull()
                val right = parts.getOrNull(1)?.toIntOrNull()
                if (parts.size != 2 || left == null || right == null) {
                    throw IllegalArgumentException(
                        "Ожидается формат «1-2 2-3 3-1» (всего пар: $size)"
                    )
                }
                if (left !in 1..size || right !in 1..size) {
                    throw IllegalArgumentException("Номера должны быть в диапазоне 1..$size")
                }
                (left - 1) to (right - 1)
            }

        if (pairs.size != size) {
            throw IllegalArgumentException("Нужно указать все $size пар, получено ${pairs.size}")
        }
        if (pairs.distinctBy { it.first }.size != size) {
            throw IllegalArgumentException("Каждый элемент слева должен встречаться один раз")
        }
        if (pairs.distinctBy { it.second }.size != size) {
            throw IllegalArgumentException("Каждый элемент справа должен встречаться один раз")
        }
        return Answer.Matching(pairs.toMap())
    }
}