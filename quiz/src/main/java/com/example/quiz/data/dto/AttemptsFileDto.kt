package com.example.quiz.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class AttemptsFileDto(val attempts: List<AttemptDto> = emptyList())