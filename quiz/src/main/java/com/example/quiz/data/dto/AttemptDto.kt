package com.example.quiz.data.dto

import com.example.quiz.domain.AnswerReport
import com.example.quiz.domain.AttemptRecord
import com.example.quiz.domain.AttemptStatus
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class AttemptDto(
    val id: String,
    val studentLogin: String,
    val themeName: String,
    val startedAt: String,
    val finishedAt: String,
    val status: String,
    val answers: List<AnswerReportDto>,
) {
    fun toDomain(): AttemptRecord{
        return AttemptRecord(
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
    }
}

