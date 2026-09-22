package com.example.quiz.domain

import java.time.Instant

enum class AttemptStatus(val title: String) {
    FINISHED("завершена"),
    INTERRUPTED("прервана"),
    TIMED_OUT("время вышло"),
}

/** Одна строка отчёта: всё, что нужно для анализа без обращения к банку вопросов. */
data class AnswerReport(
    val questionId: String,
    val questionText: String,
    val studentAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
)

data class AttemptRecord(
    val id: String,
    val studentLogin: String,
    val themeName: String,
    val startedAt: Instant,
    val finishedAt: Instant,
    val status: AttemptStatus,
    val answers: List<AnswerReport>,
) {
    val score: Int get() = answers.count { it.isCorrect }
    val total: Int get() = answers.size

    val correctCount: Int get() = answers.count { it.isCorrect }
    val wrongCount: Int get() = answers.count { !it.isCorrect }
}