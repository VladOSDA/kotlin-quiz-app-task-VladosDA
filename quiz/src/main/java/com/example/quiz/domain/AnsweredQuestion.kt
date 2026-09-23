package com.example.quiz.domain

data class AnsweredQuestion(
    val questionId: String,
    val answer: Answer,
    val isCorrect: Boolean
)