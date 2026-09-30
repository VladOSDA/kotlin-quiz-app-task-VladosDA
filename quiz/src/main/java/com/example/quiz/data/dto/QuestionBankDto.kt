package com.example.quiz.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class QuestionBankDto(
    val themes: List<String> = emptyList(),
    val questions: List<QuestionsDto> = emptyList(),
)