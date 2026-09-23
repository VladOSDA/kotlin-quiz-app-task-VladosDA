package com.example.quiz.engine

import com.example.quiz.data.AttemptRepository
import com.example.quiz.data.QuestionRepository
import com.example.quiz.domain.QuizConfig
import com.example.quiz.engine.utils.AnswerFormatter
import com.example.quiz.domain.AnswerReport
import com.example.quiz.domain.AnsweredQuestion
import com.example.quiz.domain.Attempt
import com.example.quiz.domain.AttemptRecord
import com.example.quiz.domain.AttemptStatus
import com.example.quiz.domain.MapOptions
import com.example.quiz.domain.MultipleChoice
import com.example.quiz.domain.OpenQuestion
import com.example.quiz.domain.QuestionTheme
import com.example.quiz.domain.QuestionType
import com.example.quiz.domain.Student
import com.example.quiz.domain.ThemeInfo
import com.example.quiz.engine.utils.MapOptionsParseStrategy
import com.example.quiz.engine.utils.MultipleChoiceParseStrategy
import com.example.quiz.engine.utils.OpenQuestionParseStrategy
import java.time.Instant
import java.util.UUID
import kotlin.time.Duration
import kotlin.time.TimeMark
import kotlin.time.TimeSource

class QuizEngine(
    private val questionRepository: QuestionRepository,
    private val attemptRepository: AttemptRepository,
    private val config: QuizConfig,
    private val timeSource: TimeSource = TimeSource.Monotonic,
) {

    private val students = mutableMapOf<String, Student>()
    private var currentStudent: Student? = null

    private var attempt: Attempt? = null
    private var currentTheme: QuestionTheme? = null
    private var themeQuestions: List<QuestionType> = emptyList()

    private var startMark: TimeMark? = null
    private var startedAt: Instant? = null
    private var saved = false
    private var timedOutQuestion: QuestionType? = null

    var lastRecord: AttemptRecord? = null
        private set

    val currentStudentName: String? get() = currentStudent?.login
    val currentThemeName: String? get() = currentTheme?.themeName
    val questionLimit: Int get() = config.questionsPerTheme

    val hasAttempt: Boolean get() = attempt != null
    val isAttemptFinished: Boolean get() = syncTimeout()?.isFinished == true
    val attemptAnswered: Int get() = attempt?.answeredCount ?: 0
    val attemptScore: Int get() = attempt?.score ?: 0
    val currentQuestion: QuestionType? get() = syncTimeout()?.currentQuestion

    val ladderStep: Int? get() = attempt?.ladderStep
    val ladderSize: Int? get() = attempt?.ladderSize

    val timeRemaining: Duration?
        get() {
            val started = startMark ?: return null
            return (config.timeLimit - started.elapsedNow()).coerceAtLeast(Duration.ZERO)
        }

    // ───── Студент и история ─────

    fun login(login: String): Student {
        val name = login.trim().takeIf { it.isNotBlank() }
            ?: throw IllegalArgumentException("Использование: register <логин>")
        val student = students.getOrPut(name) { Student(name) }
        currentStudent = student
        discardAttempt()
        return student
    }

    fun history(): List<AttemptRecord> =
        currentStudent?.let { attemptRepository.byStudent(it.login) }.orEmpty()

    fun findRecord(id: String): AttemptRecord? =
        history().firstOrNull { it.id.equals(id, ignoreCase = true) }

    // ───── Темы ─────

    /** Темы, в которых есть хотя бы один вопрос. */
    fun availableThemes(): List<QuestionTheme> =
        questionRepository.themes().filter { questionRepository.byTheme(it).isNotEmpty() }

    fun themeOverview(): List<ThemeInfo> {
        val login = currentStudent?.login
        return availableThemes().map { theme ->
            val questions = questionRepository.byTheme(theme)
            val levels = questions.map { it.difficultyLevel }
            val attempts = login
                ?.let { attemptRepository.byStudent(it).filter { a -> a.themeName == theme.themeName } }
                .orEmpty()
            ThemeInfo(
                theme = theme,
                questionCount = questions.size,
                ladderSize = if (levels.isEmpty()) 0 else levels.max() - levels.min() + 1,
                attemptCount = attempts.size,
                bestScore = attempts.maxOfOrNull { it.score },
            )
        }
    }

    /** Разбор ввода пользователя: номер темы из списка или её название. */
    fun resolveTheme(input: String): QuestionTheme {
        val themes = availableThemes()
        require(themes.isNotEmpty()) { "В банке вопросов нет ни одной темы с вопросами" }

        val number = input.trim().toIntOrNull()
        if (number != null) {
            return themes.getOrNull(number - 1)
                ?: throw IllegalArgumentException("Темы с номером $number нет — доступны 1..${themes.size}")
        }
        return themes.firstOrNull { it.themeName.equals(input.trim(), ignoreCase = true) }
            ?: throw IllegalArgumentException("Тема «${input.trim()}» не найдена. themes — список тем")
    }

    // ───── Прохождение ─────

    fun start(theme: QuestionTheme) {
        requireRegistered()
        if (hasAttempt && !isAttemptFinished) {
            throw IllegalArgumentException("Попытка уже идёт — сначала введите reset")
        }
        val questions = questionRepository.byTheme(theme)
        if (questions.isEmpty()) {
            throw IllegalArgumentException("В теме «${theme.themeName}» нет вопросов")
        }

        currentTheme = theme
        themeQuestions = questions
        attempt = Attempt(questions, config.questionsPerTheme)
        startMark = timeSource.markNow()
        startedAt = Instant.now()
        saved = false
        timedOutQuestion = null
        lastRecord = null
    }

    fun answer(input: String): AnsweredQuestion {
        requireRegistered()
        val attempt = activeAttempt()
        val question = requireNotNull(attempt.currentQuestion) { "Нет текущего вопроса" }

        val parsingStrategy = when(question){
            is MultipleChoice -> MultipleChoiceParseStrategy()
            is OpenQuestion -> OpenQuestionParseStrategy()
            is MapOptions -> MapOptionsParseStrategy()
        }

        val answer = parsingStrategy.parseUserInput(question = question, raw=input.trim())

        attempt.addAnswer( answer)
        val recorded = attempt.answers.last { it.questionId == question.id }

        if (attempt.isFinished) finalizeAttempt(AttemptStatus.FINISHED)
        return recorded
    }

    fun reset() {
        requireRegistered()
        if (!hasAttempt) {
            throw IllegalArgumentException("Активной попытки нет — введите start")
        }
        finalizeAttempt(AttemptStatus.INTERRUPTED)
        discardAttempt()
    }

    // ───── Внутреннее ─────

    private fun activeAttempt(): Attempt {
        val attempt = syncTimeout()
            ?: throw IllegalArgumentException("Тест не начат — введите start")
        if (attempt.isFinished) {
            throw IllegalArgumentException("Попытка завершена — введите reset, чтобы начать заново")
        }
        return attempt
    }

    private fun syncTimeout(): Attempt? {
        val attempt = attempt ?: return null
        val started = startMark ?: return attempt

        if (!attempt.isFinished && started.elapsedNow() >= config.timeLimit) {
            timedOutQuestion = attempt.currentQuestion
            attempt.timeout = true
            finalizeAttempt(AttemptStatus.TIMED_OUT)
        }
        return attempt
    }

    private fun finalizeAttempt(status: AttemptStatus) {
        if (saved) return
        val attempt = attempt ?: return
        val student = currentStudent ?: return
        val theme = currentTheme ?: return

        val record = AttemptRecord(
            id = UUID.randomUUID().toString().take(8),
            studentLogin = student.login,
            themeName = theme.themeName,
            startedAt = startedAt ?: Instant.now(),
            finishedAt = Instant.now(),
            status = status,
            answers = buildReports(attempt, status),
        )
        attemptRepository.save(record)
        saved = true
        lastRecord = record
    }

    private fun buildReports(attempt: Attempt, status: AttemptStatus): List<AnswerReport> {
        val reports = attempt.answers.mapNotNull { answered ->
            val question = themeQuestions.firstOrNull { it.id == answered.questionId }
                ?: return@mapNotNull null
            AnswerReport(
                questionId = question.id,
                questionText = question.description,
                studentAnswer = AnswerFormatter.studentAnswer(question, answered.answer),
                correctAnswer = AnswerFormatter.correctAnswer(question),
                isCorrect = answered.isCorrect,
            )
        }

        val pending = timedOutQuestion
        if (status == AttemptStatus.TIMED_OUT && pending != null) {
            return reports + AnswerReport(
                questionId = pending.id,
                questionText = pending.description,
                studentAnswer = "— (время вышло)",
                correctAnswer = AnswerFormatter.correctAnswer(pending),
                isCorrect = false,
            )
        }
        return reports
    }

    private fun discardAttempt() {
        attempt = null
        currentTheme = null
        themeQuestions = emptyList()
        startMark = null
        startedAt = null
        saved = false
        timedOutQuestion = null
    }

    private fun requireRegistered() {
        if (currentStudent == null) {
            throw IllegalArgumentException("Сначала зарегистрируйтесь: register <логин>")
        }
    }
}