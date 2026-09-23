package com.example.quiz.domain

import kotlin.time.Duration

data class QuizConfig(val timeLimit: Duration, val questionsPerTheme: Int) {
    init {
        require(questionsPerTheme > 0) { "Лимит вопросов на тему должен быть больше 0" }
        require(timeLimit.isPositive()) { "Лимит времени должен быть больше 0" }
    }
}
