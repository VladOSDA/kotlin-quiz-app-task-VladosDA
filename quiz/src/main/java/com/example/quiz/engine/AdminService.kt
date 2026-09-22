package com.example.quiz.engine

import com.example.quiz.data.AttemptRepository
import com.example.quiz.data.ConfigRepository
import com.example.quiz.data.JsonStorage
import com.example.quiz.data.QuestionRepository
import com.example.quiz.data.QuizConfig
import com.example.quiz.data.dto.ExportDto
import com.example.quiz.data.dto.QuestionBankDto
import com.example.quiz.data.dto.toDto
import com.example.quiz.domain.AttemptRecord
import com.example.quiz.domain.QuestionTheme
import com.example.quiz.domain.QuestionType
import java.nio.file.Path
import java.time.Instant

class AdminService(
    private val questions: QuestionRepository,
    private val attempts: AttemptRepository,
    private val config: ConfigRepository,
    private val storage: JsonStorage,
) {

    // ───── Темы ─────

    fun themes(): List<QuestionTheme> = questions.themes()

    fun addTheme(name: String) {
        val theme = QuestionTheme(name.trim())
        questions.addTheme(theme)
    }

    fun removeTheme(name: String): Boolean =
        questions.removeTheme(QuestionTheme(name.trim()))

    fun resolveTheme(input: String): QuestionTheme {
        val all = themes()
        val number = input.trim().toIntOrNull()
        if (number != null) {
            return all.getOrNull(number - 1)
                ?: throw IllegalArgumentException("Темы с номером $number нет — доступны 1..${all.size}")
        }
        return all.firstOrNull { it.themeName.equals(input.trim(), ignoreCase = true) }
            ?: QuestionTheme(input.trim())   // новая тема создаётся вместе с вопросом
    }

    // ───── Вопросы ─────

    fun allQuestions(): List<QuestionType> = questions.all()

    fun questionsOf(theme: QuestionTheme): List<QuestionType> = questions.byTheme(theme)

    fun question(id: String): QuestionType? = questions.byId(id)

    fun addQuestion(question: QuestionType) = questions.add(question)

    fun updateQuestion(question: QuestionType) = questions.update(question)

    fun removeQuestion(id: String): Boolean = questions.remove(id.trim())

    // ───── Параметры теста ─────

    fun settings(): QuizConfig = config.get()

    fun setTimeLimit(minutes: Int) {
        require(minutes > 0) { "Лимит времени должен быть больше 0" }
        config.update(minutes, config.get().questionsPerTheme)
    }

    fun setQuestionLimit(count: Int) {
        require(count > 0) { "Лимит вопросов должен быть больше 0" }
        config.update(config.get().timeLimit.inWholeMinutes.toInt(), count)
    }

    // ───── Попытки ─────

    fun students(): List<String> = attempts.students()

    fun allAttempts(): List<AttemptRecord> = attempts.all()

    fun attemptsOf(login: String): List<AttemptRecord> = attempts.byStudent(login.trim())

    fun attempt(id: String): AttemptRecord? = attempts.byId(id.trim())

    fun removeAttempt(id: String): Boolean = attempts.remove(id.trim())

    // ───── Выгрузка ─────

    fun export(fileName: String = "export.json"): Path {
        val dto = ExportDto(
            exportedAt = Instant.now().toString(),
            config = config.dto(),
            questionBank = QuestionBankDto(
                themes = questions.themes().map { it.themeName },
                questions = questions.all().map { it.toDto() },
            ),
            attempts = attempts.all().map { it.toDto() },
        )
        storage.save(fileName, ExportDto.serializer(), dto)
        return storage.path(fileName).toAbsolutePath()
    }
}