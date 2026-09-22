package com.example.quiz.data

import com.example.quiz.data.dto.QuestionBankDto
import com.example.quiz.data.dto.toDomain
import com.example.quiz.data.dto.toDto
import com.example.quiz.domain.QuestionTheme
import com.example.quiz.domain.QuestionType

class QuestionRepository(private val storage: JsonStorage) {

    private val questions = mutableListOf<QuestionType>()
    private val themes = mutableListOf<QuestionTheme>()

    init {
        val bank = storage.load(FILE_NAME, QuestionBankDto.serializer()) { DemoData.questionBank() }
        questions += bank.questions.map { it.toDomain() }
        themes += bank.themes.map { QuestionTheme(it) }
        // темы, встречающиеся у вопросов, но отсутствующие в списке тем
        themes += questions.map { it.theme }.distinct().filter { it !in themes }

        val duplicates = questions.groupingBy { it.id }.eachCount().filterValues { it > 1 }.keys
        require(duplicates.isEmpty()) { "В банке вопросов повторяются id: $duplicates" }
    }

    fun all(): List<QuestionType> = questions.toList()

    fun themes(): List<QuestionTheme> = themes.toList()

    fun byId(id: String): QuestionType? = questions.firstOrNull { it.id == id }

    fun byTheme(theme: QuestionTheme): List<QuestionType> = questions.filter { it.theme == theme }

    fun countByDifficulty(theme: QuestionTheme): Map<Int, Int> =
        byTheme(theme).groupingBy { it.difficultyLevel }.eachCount().toSortedMap()

    fun add(question: QuestionType) {
        require(byId(question.id) == null) { "Вопрос с id «${question.id}» уже существует" }
        questions += question
        if (question.theme !in themes) themes += question.theme
        persist()
    }

    fun update(question: QuestionType) {
        val index = questions.indexOfFirst { it.id == question.id }
        require(index >= 0) { "Вопрос с id «${question.id}» не найден" }
        questions[index] = question
        persist()
    }

    fun remove(id: String): Boolean {
        val removed = questions.removeIf { it.id == id }
        if (removed) persist()
        return removed
    }

    fun addTheme(theme: QuestionTheme) {
        require(theme !in themes) { "Тема «${theme.themeName}» уже существует" }
        themes += theme
        persist()
    }

    fun removeTheme(theme: QuestionTheme): Boolean {
        val removed = themes.remove(theme)
        if (removed) {
            questions.removeIf { it.theme == theme }
            persist()
        }
        return removed
    }

    private fun persist() {
        storage.save(
            FILE_NAME,
            QuestionBankDto.serializer(),
            QuestionBankDto(themes.map { it.themeName }, questions.map { it.toDto() }),
        )
    }

    companion object {
        const val FILE_NAME = "questions.json"
    }
}