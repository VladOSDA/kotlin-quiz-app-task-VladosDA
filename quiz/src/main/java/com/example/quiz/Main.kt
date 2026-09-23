package com.example.quiz

import com.example.quiz.data.AttemptRepository
import com.example.quiz.data.ConfigRepository
import com.example.quiz.data.JsonDataSource
import com.example.quiz.data.QuestionRepository
import com.example.quiz.engine.AdminEngine
import com.example.quiz.engine.QuizEngine
import com.example.quiz.ui.AdminCli
import com.example.quiz.ui.Cli
import java.nio.file.Paths
import kotlin.system.exitProcess

private const val DATA_DIR = "data"

fun main() {
    val storage = try {
        JsonDataSource(Paths.get(DATA_DIR))
    } catch (e: Exception) {
        println("Не удалось создать папку с данными «$DATA_DIR»: ${e.message}")
        exitProcess(1)
    }

    val questions: QuestionRepository
    val attempts: AttemptRepository
    val config: ConfigRepository
    try {
        questions = QuestionRepository(storage)
        attempts = AttemptRepository(storage)
        config = ConfigRepository(storage)
    } catch (e: Exception) {
        println("Ошибка загрузки данных: ${e.message}")
        println("Проверьте файлы в папке «$DATA_DIR» или удалите их — они будут созданы заново.")
        exitProcess(1)
    }

    val settings = config.get()

    println("Данные: ${storage.path("").toAbsolutePath()}")
    println("Вопросов: ${questions.all().size}, тем: ${questions.themes().size}, попыток в истории: ${attempts.all().size}")
    println("Лимит времени: ${settings.timeLimit}, вопросов на тему: ${settings.questionsPerTheme}")
    println()

    val engine = QuizEngine(
        questionRepository = questions,
        attemptRepository = attempts,
        config = config.get(),
    )

    val admin = AdminEngine(questions, attempts, config, storage)
    Cli(engine, AdminCli(admin)).run()
}