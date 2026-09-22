package com.example.quiz.data

import com.example.quiz.data.dto.QuizConfigDto
import kotlin.text.toInt
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

data class QuizConfig(val timeLimit: Duration, val questionsPerTheme: Int) {
    init {
        require(questionsPerTheme > 0) { "Лимит вопросов на тему должен быть больше 0" }
        require(timeLimit.isPositive()) { "Лимит времени должен быть больше 0" }
    }
}

class ConfigRepository(private val storage: JsonStorage) {

    private var config: QuizConfig

    init {
        val dto = storage.load(FILE_NAME, QuizConfigDto.serializer()) { QuizConfigDto() }
        config = QuizConfig(dto.timeLimitMinutes.minutes, dto.questionsPerTheme)
    }

    fun get(): QuizConfig = config

    fun update(timeLimitMinutes: Int, questionsPerTheme: Int) {
        config = QuizConfig(timeLimitMinutes.minutes, questionsPerTheme)
        storage.save(
            FILE_NAME,
            QuizConfigDto.serializer(),
            QuizConfigDto(timeLimitMinutes, questionsPerTheme),
        )
    }

    fun dto(): QuizConfigDto =
        QuizConfigDto(config.timeLimit.inWholeMinutes.toInt(), config.questionsPerTheme)

    companion object {
        const val FILE_NAME = "config.json"
    }
}
