package com.example.quiz.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuizConfigDto(
    val timeLimitMinutes: Int = 15,
    val questionsPerTheme: Int = 10,
)