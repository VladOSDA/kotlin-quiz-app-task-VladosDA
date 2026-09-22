package com.example.quiz.data

import com.example.quiz.data.dto.AttemptsFileDto
import com.example.quiz.data.dto.toDomain
import com.example.quiz.data.dto.toDto
import com.example.quiz.domain.AttemptRecord

class AttemptRepository(private val storage: JsonStorage) {

    private val attempts = mutableListOf<AttemptRecord>()

    init {
        val file = storage.load(FILE_NAME, AttemptsFileDto.serializer()) { AttemptsFileDto() }
        attempts += file.attempts.map { it.toDomain() }
    }

    fun all(): List<AttemptRecord> = attempts.sortedByDescending { it.startedAt }

    fun byStudent(login: String): List<AttemptRecord> =
        attempts.filter { it.studentLogin == login }.sortedByDescending { it.startedAt }

    fun byId(id: String): AttemptRecord? = attempts.firstOrNull { it.id == id }

    fun students(): List<String> = attempts.map { it.studentLogin }.distinct().sorted()

    fun countFor(login: String, themeName: String): Int =
        attempts.count { it.studentLogin == login && it.themeName == themeName }

    fun save(record: AttemptRecord) {
        attempts += record
        persist()
    }

    fun remove(id: String): Boolean {
        val removed = attempts.removeIf { it.id == id }
        if (removed) persist()
        return removed
    }

    private fun persist() {
        storage.save(FILE_NAME, AttemptsFileDto.serializer(), AttemptsFileDto(attempts.map { it.toDto() }))
    }

    companion object {
        const val FILE_NAME = "attempts.json"
    }
}