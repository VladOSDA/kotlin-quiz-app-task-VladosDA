package com.example.quiz.domain.questions

import com.example.quiz.domain.Answer
import com.example.quiz.domain.QuestionTheme



sealed interface QuestionType{
    val theme: QuestionTheme

    val id: String
    val difficultyLevel: Int
    val description: String
    fun checkCorrect(answer: Answer): Boolean
}


