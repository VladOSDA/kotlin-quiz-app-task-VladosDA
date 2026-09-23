package com.example.quiz.domain

import java.time.Instant

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