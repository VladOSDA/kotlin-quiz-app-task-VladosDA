package com.example.quiz.ui

import com.example.quiz.domain.MapOptions
import com.example.quiz.domain.MultipleChoice
import com.example.quiz.domain.OpenQuestion
import com.example.quiz.domain.QuestionType
import com.example.quiz.engine.AdminService
import java.time.ZoneId
import java.time.format.DateTimeFormatter

private val adminDateFormat =
    DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm").withZone(ZoneId.systemDefault())

class AdminCli(private val admin: AdminService) {

    /** Возвращает false, если админ вышел из режима. */
    fun dispatch(head: String, arg: String?): Boolean {
        when (head) {
            "help" -> printHelp()
            "themes" -> printThemes()
            "theme-add" -> addTheme(arg)
            "theme-del" -> removeTheme(arg)
            "questions" -> printQuestions(arg)
            "question-show" -> showQuestion(arg)
            "question-add" -> addQuestion()
            "question-del" -> removeQuestion(arg)
            "config" -> printConfig()
            "config-time" -> setTime(arg)
            "config-limit" -> setLimit(arg)
            "students" -> printStudents()
            "attempts" -> printAttempts(arg)
            "attempt-del" -> removeAttempt(arg)
            "export" -> export(arg)
            "logout" -> return false
            else -> println("Неизвестная команда «$head». Справка — help")
        }
        return true
    }

    // ───── Темы ─────

    private fun printThemes() {
        val themes = admin.themes()
        if (themes.isEmpty()) {
            println("Тем нет. theme-add <название>")
            return
        }
        println("Темы:")
        themes.forEachIndexed { index, theme ->
            println("  ${index + 1}) ${theme.themeName} — вопросов ${admin.questionsOf(theme).size}")
        }
    }

    private fun addTheme(arg: String?) {
        requireArg(arg, "theme-add <название>")
        admin.addTheme(arg!!)
        println("Тема «$arg» добавлена.")
    }

    private fun removeTheme(arg: String?) {
        requireArg(arg, "theme-del <название>")
        val count = admin.questionsOf(admin.resolveTheme(arg!!)).size
        if (count > 0 && !confirm("Вместе с темой удалятся $count вопрос(ов). Продолжить?")) {
            println("Отменено.")
            return
        }
        if (admin.removeTheme(arg)) println("Тема удалена.") else println("Тема не найдена.")
    }

    // ───── Вопросы ─────

    private fun printQuestions(arg: String?) {
        val list = if (arg.isNullOrBlank()) {
            admin.allQuestions()
        } else {
            admin.questionsOf(admin.resolveTheme(arg))
        }
        if (list.isEmpty()) {
            println("Вопросов нет.")
            return
        }
        println("Вопросов: ${list.size}")
        list.sortedWith(compareBy({ it.theme.themeName }, { it.difficultyLevel })).forEach {
            println("  ${it.id.padEnd(24)} ур.${it.difficultyLevel}  ${typeName(it)}  " +
                    "[${it.theme.themeName}]  ${it.description.take(50)}")
        }
    }

    private fun showQuestion(arg: String?) {
        requireArg(arg, "question-show <id>")
        val question = admin.question(arg!!.trim())
            ?: throw IllegalArgumentException("Вопрос «$arg» не найден")

        println("id:        ${question.id}")
        println("тема:      ${question.theme.themeName}")
        println("уровень:   ${question.difficultyLevel}")
        println("тип:       ${typeName(question)}")
        println("вопрос:    ${question.description}")
        when (question) {
            is MultipleChoice -> {
                question.answerOptions.forEachIndexed { i, o -> println("  ${i + 1}) $o") }
                println("правильный: ${question.correctAnswer}")
            }
            is OpenQuestion -> println("эталон:    ${question.correctAnswer}")
            is MapOptions -> {
                question.correctAnswer.entries.sortedBy { it.key }.forEach { (l, r) ->
                    println("  ${question.list1[l]} — ${question.list2[r]}")
                }
            }
        }
    }

    private fun removeQuestion(arg: String?) {
        requireArg(arg, "question-del <id>")
        if (admin.removeQuestion(arg!!)) println("Вопрос удалён.") else println("Вопрос не найден.")
    }

    /** Пошаговое добавление вопроса. */
    private fun addQuestion() {
        val type = ask("Тип (1 — тестовый, 2 — открытый, 3 — соответствие)")
        val id = ask("id вопроса (латиницей, без пробелов)").trim()
        if (admin.question(id) != null) throw IllegalArgumentException("Вопрос с id «$id» уже есть")

        printThemes()
        val theme = admin.resolveTheme(ask("Тема (номер, название или новое название)"))
        val level = ask("Уровень сложности (0..5)").trim().toIntOrNull()
            ?: throw IllegalArgumentException("Уровень должен быть числом")
        val description = ask("Формулировка вопроса")

        val question: QuestionType = when (type.trim()) {
            "1" -> {
                val options = askList("Вариант ответа (пустая строка — закончить)")
                val correct = ask("Номер правильного варианта (1..${options.size})")
                    .trim().toIntOrNull()
                    ?: throw IllegalArgumentException("Номер должен быть числом")
                MultipleChoice(id, theme, level, description, correct, options)
            }

            "2" -> OpenQuestion(id, theme, level, description, ask("Эталонный ответ"))

            "3" -> {
                val list1 = askList("Элемент левого списка (пустая строка — закончить)")
                val list2 = askList("Элемент правого списка (пустая строка — закончить)")
                val pairs = ask("Правильные пары в формате «1-2 2-3 3-1»")
                    .split(Regex("[,\\s]+")).filter { it.isNotBlank() }
                    .associate { token ->
                        val parts = token.split("-")
                        val left = parts.getOrNull(0)?.toIntOrNull()
                        val right = parts.getOrNull(1)?.toIntOrNull()
                        if (parts.size != 2 || left == null || right == null) {
                            throw IllegalArgumentException("Неверный формат пары «$token»")
                        }
                        (left - 1) to (right - 1)
                    }
                MapOptions(id, theme, level, description, pairs, list1, list2)
            }

            else -> throw IllegalArgumentException("Неизвестный тип «$type»")
        }

        admin.addQuestion(question)
        println("Вопрос «$id» добавлен и сохранён в questions.json.")
    }

    // ───── Параметры ─────

    private fun printConfig() {
        val settings = admin.settings()
        println("Лимит времени:      ${settings.timeLimit}")
        println("Вопросов на тему:   ${settings.questionsPerTheme}")
        println("config-time <минуты> / config-limit <вопросов> — изменить")
    }

    private fun setTime(arg: String?) {
        requireArg(arg, "config-time <минуты>")
        admin.setTimeLimit(arg!!.trim().toIntOrNull()
            ?: throw IllegalArgumentException("Минуты должны быть числом"))
        println("Лимит времени обновлён. Применится к новым попыткам.")
    }

    private fun setLimit(arg: String?) {
        requireArg(arg, "config-limit <вопросов>")
        admin.setQuestionLimit(arg!!.trim().toIntOrNull()
            ?: throw IllegalArgumentException("Количество должно быть числом"))
        println("Лимит вопросов обновлён. Применится к новым попыткам.")
    }

    // ───── Попытки ─────

    private fun printStudents() {
        val students = admin.students()
        if (students.isEmpty()) {
            println("Студентов пока нет.")
            return
        }
        println("Студенты:")
        students.forEach { login ->
            val records = admin.attemptsOf(login)
            println("  $login — попыток ${records.size}, лучший балл ${records.maxOfOrNull { it.score } ?: 0}")
        }
    }

    private fun printAttempts(arg: String?) {
        val records = if (arg.isNullOrBlank()) admin.allAttempts() else admin.attemptsOf(arg)
        if (records.isEmpty()) {
            println("Попыток нет.")
            return
        }
        println("Попыток: ${records.size}")
        records.forEach {
            println("  ${it.id}  ${adminDateFormat.format(it.startedAt)}  ${it.studentLogin.padEnd(14)} " +
                    "${it.themeName} — ${it.status.title}, ${it.score} из ${it.total}")
        }
    }

    private fun removeAttempt(arg: String?) {
        requireArg(arg, "attempt-del <id>")
        val record = admin.attempt(arg!!)
            ?: throw IllegalArgumentException("Попытка «$arg» не найдена")
        if (!confirm("Удалить попытку ${record.id} студента «${record.studentLogin}»?")) {
            println("Отменено.")
            return
        }
        admin.removeAttempt(arg)
        println("Попытка удалена.")
    }

    private fun export(arg: String?) {
        val fileName = arg?.trim()?.takeIf { it.isNotEmpty() } ?: "export.json"
        val path = admin.export(fileName)
        println("Выгружено: $path")
        println("Файл содержит банк вопросов, параметры теста и все попытки всех студентов.")
    }

    // ───── Ввод ─────

    private fun ask(prompt: String): String {
        print("$prompt: ")
        return readlnOrNull() ?: throw IllegalArgumentException("Ввод прерван")
    }

    private fun askList(prompt: String): List<String> {
        val items = mutableListOf<String>()
        while (true) {
            print("$prompt [${items.size + 1}]: ")
            val line = readlnOrNull() ?: break
            if (line.isBlank()) break
            items += line.trim()
        }
        return items
    }

    private fun confirm(question: String): Boolean {
        print("$question (да/нет): ")
        val answer = readlnOrNull()?.trim()?.lowercase()
        return answer == "да" || answer == "y" || answer == "yes"
    }

    private fun requireArg(arg: String?, usage: String) {
        if (arg.isNullOrBlank()) throw IllegalArgumentException("Использование: $usage")
    }

    private fun typeName(question: QuestionType): String = when (question) {
        is MultipleChoice -> "тестовый"
        is OpenQuestion -> "открытый"
        is MapOptions -> "соответствие"
    }

    private fun printHelp() {
        println(
            """
            Режим администратора:
              themes                  — список тем
              theme-add <название>    — добавить тему
              theme-del <название>    — удалить тему вместе с вопросами
              questions [тема]        — список вопросов
              question-show <id>      — показать вопрос целиком
              question-add            — добавить вопрос (пошагово)
              question-del <id>       — удалить вопрос
              config                  — параметры теста
              config-time <минуты>    — лимит времени
              config-limit <вопросов> — лимит вопросов на тему
              students                — студенты и их результаты
              attempts [логин]        — попытки (все или одного студента)
              attempt-del <id>        — удалить попытку (пересдача)
              export [имя файла]      — выгрузить всё в JSON
              logout                  — выйти из режима администратора
              exit                    — выход из программы
            """.trimIndent()
        )
    }
}