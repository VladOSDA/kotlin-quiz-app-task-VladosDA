package com.example.quiz.domain

enum class AttemptStatus(val title: String) {
    FINISHED("завершена"),
    INTERRUPTED("прервана"),
    TIMED_OUT("время вышло"),
}