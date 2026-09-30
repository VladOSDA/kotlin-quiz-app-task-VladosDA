package com.example.quiz.domain.questions

import com.example.quiz.domain.Answer
import com.example.quiz.domain.QuestionTheme



sealed interface QuestionType{
    val theme: QuestionTheme

    val id: String
    val difficultyLevel: Int
    val description: String
    fun checkCorrect(answer: Answer): Boolean

    companion object{
        const val MIN_DIFFICULTY_LEVEL = 0
        const val MAX_DIFFICULTY_LEVEL = 5

        internal fun requireValidDifficulty(level: Int, description: String) {
            require(level in MIN_DIFFICULTY_LEVEL..MAX_DIFFICULTY_LEVEL) {
                "У задания <<$description>> уровень сложности вне диапазона ${MIN_DIFFICULTY_LEVEL}..${MAX_DIFFICULTY_LEVEL}"
            }
        }

    }
}


