package com.example.quiz.data

import com.example.quiz.data.dto.QuizConfigDto
import com.example.quiz.domain.QuizConfig
import kotlin.time.Duration.Companion.minutes


class ConfigRepository(private val storage: JsonDataSource,
                       var fileName: String = DEFAULT_FILE_NAME) {

    private var config: QuizConfig

    private val fileNameWithExtension: String
        get() = "$fileName.json".trim()

    init {
        val dto = storage.load(fileNameWithExtension, QuizConfigDto.serializer()) { QuizConfigDto() }
        config = QuizConfig(dto.timeLimitMinutes.minutes, dto.questionsPerTheme)
    }

    fun get(): QuizConfig = config

    fun update(timeLimitMinutes: Int, questionsPerTheme: Int) {
        config = QuizConfig(timeLimitMinutes.minutes, questionsPerTheme)
        storage.save(
            fileNameWithExtension,
            QuizConfigDto.serializer(),
            QuizConfigDto(timeLimitMinutes, questionsPerTheme),
        )
    }

    fun dto(): QuizConfigDto =
        QuizConfigDto(config.timeLimit.inWholeMinutes.toInt(), config.questionsPerTheme)

    companion object {
        const val DEFAULT_FILE_NAME = "config.json"
    }
}
