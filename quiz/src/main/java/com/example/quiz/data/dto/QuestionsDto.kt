package com.example.quiz.data.dto

import com.example.quiz.domain.questions.MapOptions
import com.example.quiz.domain.questions.MultipleChoice
import com.example.quiz.domain.questions.OpenQuestion
import com.example.quiz.domain.QuestionTheme
import com.example.quiz.domain.questions.QuestionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
sealed class QuestionsDto {
    abstract val id: String
    abstract val theme: String
    abstract val difficultyLevel: Int
    abstract val description: String

    @Serializable
    @SerialName("choice")
    data class Choice(
        override val id: String,
        override val theme: String,
        override val difficultyLevel: Int,
        override val description: String,
        val answerOptions: List<String>,
        val correctAnswer: Int,
    ) : QuestionsDto()

    @Serializable
    @SerialName("open")
    data class Open(
        override val id: String,
        override val theme: String,
        override val difficultyLevel: Int,
        override val description: String,
        val correctAnswer: String,
    ) : QuestionsDto()

    @Serializable
    @SerialName("matching")
    data class Matching(
        override val id: String,
        override val theme: String,
        override val difficultyLevel: Int,
        override val description: String,
        val list1: List<String>,
        val list2: List<String>,
        val correctAnswer: Map<Int, Int>
    ) : QuestionsDto()

    fun toDomain(): QuestionType = when (this) {
        is Choice -> MultipleChoice(
            id = id,
            theme = QuestionTheme(theme),
            difficultyLevel = difficultyLevel,
            description = description,
            answerOptions = answerOptions,
            correctAnswer = correctAnswer,
        )

        is Open -> OpenQuestion(
            id = id,
            theme = QuestionTheme(theme),
            difficultyLevel = difficultyLevel,
            description = description,
            correctAnswer = correctAnswer,
        )

        is Matching -> MapOptions(
            id = id,
            theme = QuestionTheme(theme),
            difficultyLevel = difficultyLevel,
            description = description,
            list1 = list1,
            list2 = list2,
            correctAnswer = correctAnswer,
        )
    }
}



