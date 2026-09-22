package com.example.quiz.data.dto

import kotlinx.serialization.Serializable

/** Самодостаточный файл для анализа: банк вопросов + все попытки всех студентов. */
@Serializable
data class ExportDto(
    val exportedAt: String,
    val config: QuizConfigDto,
    val questionBank: QuestionBankDto,
    val attempts: List<AttemptDto>,
)