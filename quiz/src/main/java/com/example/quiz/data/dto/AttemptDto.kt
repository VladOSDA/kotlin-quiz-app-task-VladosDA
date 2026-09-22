package com.example.quiz.data.dto

import com.example.quiz.domain.AnswerReport
import com.example.quiz.domain.AttemptRecord
import com.example.quiz.domain.AttemptStatus
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class AttemptsFileDto(val attempts: List<AttemptDto> = emptyList())

@Serializable
data class AttemptDto(
    val id: String,
    val studentLogin: String,
    val themeName: String,
    val startedAt: String,
    val finishedAt: String,
    val status: String,
    val answers: List<AnswerReportDto>,
)

@Serializable
data class AnswerReportDto(
    val questionId: String,
    val questionText: String,
    val studentAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
)

fun AttemptDto.toDomain(): AttemptRecord = AttemptRecord(
    id = id,
    studentLogin = studentLogin,
    themeName = themeName,
    startedAt = Instant.parse(startedAt),
    finishedAt = Instant.parse(finishedAt),
    status = AttemptStatus.valueOf(status),
    answers = answers.map {
        AnswerReport(it.questionId, it.questionText, it.studentAnswer, it.correctAnswer, it.isCorrect)
    },
)

fun AttemptRecord.toDto(): AttemptDto = AttemptDto(
    id = id,
    studentLogin = studentLogin,
    themeName = themeName,
    startedAt = startedAt.toString(),
    finishedAt = finishedAt.toString(),
    status = status.name,
    answers = answers.map {
        AnswerReportDto(it.questionId, it.questionText, it.studentAnswer, it.correctAnswer, it.isCorrect)
    },
)