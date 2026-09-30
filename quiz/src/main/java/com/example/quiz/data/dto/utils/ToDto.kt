package com.example.quiz.data.dto.utils

import com.example.quiz.data.dto.AnswerReportDto
import com.example.quiz.data.dto.AttemptDto
import com.example.quiz.data.dto.QuestionsDto
import com.example.quiz.domain.AttemptRecord
import com.example.quiz.domain.questions.MapOptions
import com.example.quiz.domain.questions.MultipleChoice
import com.example.quiz.domain.questions.OpenQuestion
import com.example.quiz.domain.questions.QuestionType

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

fun QuestionType.toDto(): QuestionsDto = when (this) {
    is MultipleChoice -> QuestionsDto.Choice(
        id, theme.themeName, difficultyLevel, description, answerOptions, correctAnswer
    )

    is OpenQuestion -> QuestionsDto.Open(
        id, theme.themeName, difficultyLevel, description, correctAnswer
    )

    is MapOptions -> QuestionsDto.Matching(
        id, theme.themeName, difficultyLevel, description, list1, list2,correctAnswer
    )
}