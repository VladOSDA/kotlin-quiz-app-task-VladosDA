package com.example.quiz.ui

import com.example.quiz.domain.AttemptRecord
import com.example.quiz.domain.MapOptions
import com.example.quiz.domain.MultipleChoice
import com.example.quiz.domain.OpenQuestion
import com.example.quiz.domain.QuestionType
import com.example.quiz.engine.QuizEngine
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import kotlin.time.Duration

private val separator = "=".repeat(60)
private val dateFormat = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

private val COMMANDS = setOf(
    "help", "register", "themes", "start", "status", "history", "report", "reset", "exit", "quit", "summary"
)

private const val ADMIN_LOGIN = "admin"

class Cli(
    private val engine: QuizEngine,
    private val adminCli: AdminCli,
)  {
    private var adminMode = false

    fun run() {
        println(banner())
        printHelp()
        var running = true
        while (running) {
            print("> ")
            val line = readlnOrNull() ?: break
            if (line.isBlank()) continue
            try {
                running = dispatch(line.trim())
            } catch (e: IllegalArgumentException) {
                println("Ошибка: ${e.message}")
            } catch (e: IllegalStateException) {
                println("Ошибка: ${e.message}")
            }
        }
        if (engine.hasAttempt && !engine.isAttemptFinished) {
            runCatching { engine.reset() }
            println("Незавершённая попытка сохранена как прерванная.")
        }
        println("До свидания!")
    }

    private fun dispatch(line: String): Boolean {
        val forced = line.startsWith("/")
        val text = if (forced) line.drop(1).trim() else line

        val head = text.substringBefore(' ').lowercase()
        val arg = text.substringAfter(' ', "").trim().takeIf { it.isNotEmpty() }

        if (adminMode) {
            if (head == "exit" || head == "quit") return false
            if (!adminCli.dispatch(head, arg)) {
                adminMode = false
                println("Выход из режима администратора. register <логин> — войти как студент.")
            }
            return true
        }

        val questionActive = engine.currentQuestion != null && !engine.isAttemptFinished
        if (questionActive && !forced && head !in COMMANDS) {
            submitAnswer(text)
            return true
        }

        return when (head) {
            "help" -> { printHelp(); true }
            "register" -> { register(arg); true }
            "themes" -> { printThemes(); true }
            "start" -> { start(arg); true }
            "status" -> { status(); true }
            "history" -> { printHistory(); true }
            "report" -> { printReportById(arg); true }
            "summary" -> { printSummary(); true }
            "reset" -> { reset(); true }
            "exit", "quit" -> handleExit()
            else -> {
                println("Неизвестная команда «$head». Справка — help")
                true
            }
        }
    }

    // ───── Команды ─────

    private fun register(arg: String?) {
        if (arg.isNullOrBlank()) throw IllegalArgumentException("Использование: register <логин>")

        if (arg.trim().equals(ADMIN_LOGIN, ignoreCase = true)) {
            adminMode = true
            println("Режим администратора. help — список команд.")
            return
        }

        val student = engine.login(arg)
        val history = engine.history()
        if (history.isEmpty()) {
            println("Здравствуйте, «${student.login}».")
        } else {
            println("С возвращением, «${student.login}». Попыток в истории: ${history.size}.")
        }
        printThemes()
    }

    private fun printThemes() {
        val overview = engine.themeOverview()
        if (overview.isEmpty()) {
            println("В банке вопросов пока нет тем с вопросами.")
            return
        }
        println()
        println("Доступные темы:")
        overview.forEachIndexed { index, info ->
            val attempts = when (info.attemptCount) {
                0 -> "попыток нет"
                else -> "попыток: ${info.attemptCount}, лучший результат: ${info.bestScore}"
            }
            println(
                "  ${index + 1}) ${info.theme.themeName} — " +
                        "вопросов ${info.questionCount}, ступеней ${info.ladderSize}, $attempts"
            )
        }
        println("start <номер или название> — начать тему")
    }

    private fun start(arg: String?) {
        if (arg.isNullOrBlank()) {
            printThemes()
            throw IllegalArgumentException("Укажите тему: start <номер или название>")
        }
        val theme = engine.resolveTheme(arg)
        engine.start(theme)
        println("Тема «${theme.themeName}». Максимум вопросов: ${engine.questionLimit}.")
        printCurrentQuestion()
    }

    private fun reset() {
        if (engine.hasAttempt && !engine.isAttemptFinished){
            engine.reset()
            println("Попытка сохранена со статусом «прервана». themes — выбрать тему заново.")
        }
        else{
            println("Нет активной попытки. themes — выбрать тему заново.")
        }
    }

    private fun handleExit(): Boolean {
        if (engine.hasAttempt && !engine.isAttemptFinished) {
            engine.reset()
            println("Попытка прервана и сохранена. Введите exit ещё раз, чтобы выйти.")
            return true
        }
        return false
    }

    private fun status() {
        when {
            engine.currentStudentName == null ->
                println("Вы не зарегистрированы. register <логин>")

            !engine.hasAttempt -> {
                println("Студент «${engine.currentStudentName}». Активной попытки нет.")
                printThemes()
            }

            engine.isAttemptFinished ->
                engine.lastRecord?.let { printReport(it) } ?: println("Попытка завершена.")

            else -> {
                println(
                    "Тема «${engine.currentThemeName}»: отвечено ${engine.attemptAnswered} " +
                            "из ${engine.questionLimit}, баллов ${engine.attemptScore}"
                )
                printCurrentQuestion()
            }
        }
    }

    private fun printHistory() {
        if (engine.currentStudentName == null) {
            throw IllegalArgumentException("Сначала зарегистрируйтесь: register <логин>")
        }
        val history = engine.history()
        if (history.isEmpty()) {
            println("История пуста. themes — выбрать тему и начать.")
            return
        }
        println()
        println("История попыток «${engine.currentStudentName}»:")
        history.forEach { record ->
            println(
                "  ${record.id}  ${dateFormat.format(record.startedAt)}  " +
                        "${record.themeName} — ${record.status.title}, " +
                        "${record.score} из ${record.total}"
            )
        }
        println("report <id> — подробный отчёт")
    }

    private fun printReportById(arg: String?) {
        if (engine.currentStudentName == null) {
            throw IllegalArgumentException("Сначала зарегистрируйтесь: register <логин>")
        }
        if (arg.isNullOrBlank()) throw IllegalArgumentException("Использование: report <id попытки>")
        val record = engine.findRecord(arg)
            ?: throw IllegalArgumentException("Попытка «$arg» не найдена. history — список попыток")
        printReport(record)
    }

    // ───── Вывод вопроса ─────

    private fun submitAnswer(input: String) {
        engine.answer(input)
        println("Ответ принят.")

        if (engine.isAttemptFinished) {
            engine.lastRecord?.let { printReport(it) }
        } else {
            printCurrentQuestion()
        }
    }

    private fun printCurrentQuestion() {
        val question = engine.currentQuestion ?: return
        val step = engine.ladderStep
        val size = engine.ladderSize
        val time = engine.timeRemaining

        println()
        println(separator)
        val header = buildString {
            append("Тема «${engine.currentThemeName}»")
            if (step != null && size != null) append(" · ступень $step из $size")
            if (time != null) append(" · осталось ${formatDuration(time)}")
        }
        println(header)
        println("Вопрос ${engine.attemptAnswered + 1}: ${question.description}")
        printBody(question)
    }

    private fun printBody(question: QuestionType) {
        when (question) {
            is MultipleChoice -> {
                question.answerOptions.forEachIndexed { index, option ->
                    println("  ${index + 1}) $option")
                }
                println("Введите номер верного ответа")
            }

            is OpenQuestion -> println("Введите ответ текстом")

            is MapOptions -> {
                question.list1.forEachIndexed { index, item ->
                    val left = "${index + 1}) $item"
                    val right = question.list2.getOrNull(index)?.let { "${index + 1}) $it" }.orEmpty()
                    println("  ${left.padEnd(36)} $right")
                }
                println("Сопоставьте элементы в формате «1-2 2-3 3-1»")
            }
        }
    }

    // ───── Отчёт ─────

    private fun printReport(record: AttemptRecord) {
        println()
        println(separator)
        println("Отчёт о попытке ${record.id}")
        println("Студент: ${record.studentLogin}")
        println("Тема: ${record.themeName}")
        println("Начата: ${dateFormat.format(record.startedAt)}")
        println("Статус: ${record.status.title}")
        println("Результат: ${record.score} из ${record.total} " +
                "(верно ${record.correctCount}, неверно ${record.wrongCount})")
        println(separator)

        record.answers.forEachIndexed { index, answer ->
            val mark = if (answer.isCorrect) "✓" else "✗"
            println("${index + 1}. $mark ${answer.questionText}")
            println("   Ваш ответ:        ${answer.studentAnswer}")
            if (!answer.isCorrect) {
                println("   Правильный ответ: ${answer.correctAnswer}")
            }
        }
        println(separator)
        println("themes — выбрать тему, history — все попытки")
    }

    private fun formatDuration(duration: Duration): String =
        duration.toComponents { minutes, seconds, _ -> "%d:%02d".format(minutes, seconds) }

    private fun printHelp() {
        println(
            """
            Команды:
              register <логин>   — вход (новый студент создаётся автоматически)
              themes             — список тем и ваши результаты по ним
              start <тема>       — начать тему (номер из списка или название)
              status             — текущий прогресс
              history            — список ваших попыток
              summary            — отчет по темам
              report <id>        — подробный отчёт о попытке
              reset              — прервать попытку (она сохранится)
              exit               — выход
              help               — эта справка

            Во время вопроса просто введите ответ. Если ответ совпадает с командой,
            поставьте перед ним «/» — например «/start».
            """.trimIndent(),
        )
    }

    private fun printSummary() {
        if (engine.currentStudentName == null) {
            throw IllegalArgumentException("Сначала зарегистрируйтесь: register <логин>")
        }
        val history = engine.history()
        if (history.isEmpty()) {
            println("История пуста.")
            return
        }
        println()
        println("Сводка по темам — «${engine.currentStudentName}»:")
        history.groupBy { it.themeName }.toSortedMap().forEach { (theme, records) ->
            val best = records.maxOf { it.score }
            val correct = records.sumOf { it.correctCount }
            val wrong = records.sumOf { it.wrongCount }
            println("  $theme — попыток ${records.size}, лучший балл $best, верно $correct, неверно $wrong")
        }
    }

    private fun banner(): String = """
        Добро пожаловать в QuizRunner
        register <логин> — чтобы начать. help — справка.
    """.trimIndent()
}