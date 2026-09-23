package com.example.quiz.data

import com.example.quiz.data.dto.AttemptsFileDto
import com.example.quiz.data.dto.toDomain
import com.example.quiz.data.dto.toDto
import com.example.quiz.domain.AttemptRecord

class AttemptRepository(private val storage: JsonDataSource,
                        var fileName: String = ConfigRepository.DEFAULT_FILE_NAME
) {

    private val attempts = mutableListOf<AttemptRecord>()

    private val fileNameWithExtension: String
        get() = "$fileName.json".trim()

    init {
        val file = storage.load(fileNameWithExtension, AttemptsFileDto.serializer()) { AttemptsFileDto() }
        attempts += file.attempts.map { it.toDomain() }
    }

    fun all(): List<AttemptRecord> = attempts.sortedByDescending { it.startedAt }

    fun byStudent(login: String): List<AttemptRecord> =
        attempts.filter { it.studentLogin == login }.sortedByDescending { it.startedAt }

    fun byId(id: String): AttemptRecord? = attempts.firstOrNull { it.id == id }

    fun students(): List<String> = attempts.map { it.studentLogin }.distinct().sorted()

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
        storage.save(fileNameWithExtension, AttemptsFileDto.serializer(), AttemptsFileDto(attempts.map { it.toDto() }))
    }

    companion object {
        const val DEFAULT_FILE_NAME = "attempts.json"
    }
}