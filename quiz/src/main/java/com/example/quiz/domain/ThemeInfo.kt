package com.example.quiz.domain

/** Сводка по теме для экрана выбора. */
data class ThemeInfo(
    val theme: QuestionTheme,
    val questionCount: Int,
    val ladderSize: Int,
    val attemptCount: Int,
    val bestScore: Int?,
)