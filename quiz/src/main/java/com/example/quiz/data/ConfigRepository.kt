package com.example.quiz.data

import com.example.quiz.data.dto.QuizConfigDto
import com.example.quiz.domain.QuizConfig
import kotlin.time.Duration.Companion.minutes


class ConfigRepository(private val storage: JsonDataSource) {

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
