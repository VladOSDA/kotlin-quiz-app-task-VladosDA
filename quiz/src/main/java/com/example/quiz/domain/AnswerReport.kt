package com.example.quiz.domain

/** Одна строка отчёта: всё, что нужно для анализа без обращения к банку вопросов. */
data class AnswerReport(
    val questionId: String,
    val questionText: String,
    val studentAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
)