package com.example.quiz.data.dto

import com.example.quiz.domain.MapOptions
import com.example.quiz.domain.MultipleChoice
import com.example.quiz.domain.OpenQuestion
import com.example.quiz.domain.QuestionTheme
import com.example.quiz.domain.QuestionType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class QuestionBankDto(
    val themes: List<String> = emptyList(),
    val questions: List<QuestionDto> = emptyList(),
)

@Serializable
data class PairDto(val from: Int, val to: Int)

@Serializable
sealed class QuestionDto {
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
        val correctOption: Int,
    ) : QuestionDto()

    @Serializable
    @SerialName("open")
    data class Open(
        override val id: String,
        override val theme: String,
        override val difficultyLevel: Int,
        override val description: String,
        val correctText: String,
    ) : QuestionDto()

    @Serializable
    @SerialName("matching")
    data class Matching(
        override val id: String,
        override val theme: String,
        override val difficultyLevel: Int,
        override val description: String,
        val list1: List<String>,
        val list2: List<String>,
        val pairs: List<PairDto>,
    ) : QuestionDto()
}

fun QuestionDto.toDomain(): QuestionType = when (this) {
    is QuestionDto.Choice -> MultipleChoice(
        id = id,
        theme = QuestionTheme(theme),
        difficultyLevel = difficultyLevel,
        description = description,
        answerOptions = answerOptions,
        correctAnswer = correctOption,
    )

    is QuestionDto.Open -> OpenQuestion(
        id = id,
        theme = QuestionTheme(theme),
        difficultyLevel = difficultyLevel,
        description = description,
        correctAnswer = correctText,
    )

    is QuestionDto.Matching -> MapOptions(
        id = id,
        theme = QuestionTheme(theme),
        difficultyLevel = difficultyLevel,
        description = description,
        list1 = list1,
        list2 = list2,
        correctAnswer = pairs.associate { it.from to it.to },
    )
}

fun QuestionType.toDto(): QuestionDto = when (this) {
    is MultipleChoice -> QuestionDto.Choice(
        id, theme.themeName, difficultyLevel, description, answerOptions, correctAnswer
    )

    is OpenQuestion -> QuestionDto.Open(
        id, theme.themeName, difficultyLevel, description, correctAnswer
    )

    is MapOptions -> QuestionDto.Matching(
        id, theme.themeName, difficultyLevel, description, list1, list2,
        correctAnswer.entries.sortedBy { it.key }.map { PairDto(it.key, it.value) }
    )
}