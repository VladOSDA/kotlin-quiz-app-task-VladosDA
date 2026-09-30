package com.example.quiz.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AnswerReportDto(
    val questionId: String,
    val questionText: String,
    val studentAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
)