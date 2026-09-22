package com.example.quiz.data

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import java.nio.file.AtomicMoveNotSupportedException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.io.path.readText
import kotlin.io.path.writeText

/** Чтение и запись JSON-файлов в папке с данными. Файл создаётся при первом обращении. */
class JsonStorage(private val dataDir: Path) {

    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
        encodeDefaults = true
        classDiscriminator = "type"
    }

    init {
        Files.createDirectories(dataDir)
    }

    fun <T> load(fileName: String, serializer: KSerializer<T>, default: () -> T): T {
        val file = dataDir.resolve(fileName)
        if (Files.notExists(file)) {
            val initial = default()
            save(fileName, serializer, initial)
            return initial
        }
        return try {
            json.decodeFromString(serializer, file.readText(Charsets.UTF_8))
        } catch (e: SerializationException) {
            throw IllegalStateException(
                "Файл «$fileName» повреждён или не соответствует формату: ${e.message}", e
            )
        }
    }

    fun <T> save(fileName: String, serializer: KSerializer<T>, value: T) {
        val file = dataDir.resolve(fileName)
        val temp = dataDir.resolve("$fileName.tmp")
        temp.writeText(json.encodeToString(serializer, value), Charsets.UTF_8)
        try {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING, StandardCopyOption.ATOMIC_MOVE)
        } catch (e: AtomicMoveNotSupportedException) {
            Files.move(temp, file, StandardCopyOption.REPLACE_EXISTING)
        }
    }

    fun path(fileName: String): Path = dataDir.resolve(fileName)
}