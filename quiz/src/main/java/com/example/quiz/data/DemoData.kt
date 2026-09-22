package com.example.quiz.data

import com.example.quiz.data.dto.PairDto
import com.example.quiz.data.dto.QuestionBankDto
import com.example.quiz.data.dto.QuestionDto

object DemoData {

    fun questionBank(): QuestionBankDto = QuestionBankDto(
        themes = listOf("Синтаксис", "Null safety", "Коллекции", "Типы", "Функции", "ООП"),
        questions = listOf(
            QuestionDto.Choice(
                id = "val-vs-var",
                theme = "Синтаксис",
                difficultyLevel = 0,
                description = "Какой синтаксис объявляет неизменяемую (read-only) переменную?",
                answerOptions = listOf("var x = 5", "val x = 5", "let x = 5", "final x = 5"),
                correctOption = 2,
            ),
            QuestionDto.Open(
                id = "entry-point",
                theme = "Синтаксис",
                difficultyLevel = 0,
                description = "Как называется функция — точка входа в Kotlin-программу?",
                correctText = "main",
            ),
            QuestionDto.Matching(
                id = "scope-functions-return",
                theme = "Функции",
                difficultyLevel = 3,
                description = "Сопоставьте scope-функцию с тем, что она возвращает",
                list1 = listOf("let", "also", "apply", "run"),
                list2 = listOf(
                    "результат лямбды",
                    "сам объект (it)",
                    "сам объект (this)",
                    "результат лямбды (this как receiver)",
                ),
                pairs = listOf(PairDto(0, 0), PairDto(1, 1), PairDto(2, 2), PairDto(3, 3)),
            ),
            QuestionDto.Choice(
                id = "int-division",
                theme = "Типы данных",
                difficultyLevel = 0,
                description = "Что вернёт выражение 10 / 3, если оба операнда типа Int?",
                answerOptions = listOf("3", "3.33", "3.0", "Ошибка компиляции"),
                correctOption = 1,
            ),

            // ───── Уровень 1 ─────
            QuestionDto.Choice(
                id = "safe-call",
                theme = "Безопасность null",
                difficultyLevel = 1,
                description = "Что делает оператор безопасного вызова ?. (например, user?.name)?",
                answerOptions = listOf(
                    "Вернёт null, если user == null",
                    "Бросит исключение, если user == null",
                    "Всегда вернёт не-null значение",
                    "Запустит корутину",
                ),
                correctOption = 1,
            ),
            QuestionDto.Choice(
                id = "elvis",
                theme = "Безопасность null",
                difficultyLevel = 1,
                description = "В каком случае выражение x ?: 5 вернёт именно 5?",
                answerOptions = listOf("Всегда", "Если x == null", "Если x == 0", "Никогда"),
                correctOption = 2,
            ),
            QuestionDto.Open(
                id = "string-template",
                theme = "Синтаксис",
                difficultyLevel = 1,
                description = "Какой символ начинает подстановку значения в строковый шаблон?",
                correctText = "$",
            ),

            // ───── Уровень 2 ─────
            QuestionDto.Choice(
                id = "list-vs-mutablelist",
                theme = "Коллекции",
                difficultyLevel = 2,
                description = "Чем List отличается от MutableList?",
                answerOptions = listOf(
                    "List можно только читать, MutableList — ещё и изменять",
                    "List может хранить только числа",
                    "Ничем, это синонимы",
                    "MutableList нельзя создать",
                ),
                correctOption = 1,
            ),
            QuestionDto.Choice(
                id = "when-expression",
                theme = "Синтаксис",
                difficultyLevel = 2,
                description = "Что произойдёт, если when используется как выражение без ветки else?",
                answerOptions = listOf(
                    "Вернётся null",
                    "Ошибка компиляции, если ветки не покрывают все случаи",
                    "Всегда ошибка компиляции",
                    "Вернётся Unit",
                ),
                correctOption = 2,
            ),
            QuestionDto.Open(
                id = "associate-by",
                theme = "Коллекции",
                difficultyLevel = 2,
                description = "Какая функция превращает List в Map, принимая лямбду-селектор ключа? (одно слово)",
                correctText = "associateBy",
            ),

            // ───── Уровень 3 ─────
            QuestionDto.Choice(
                id = "data-class-generated",
                theme = "ООП",
                difficultyLevel = 3,
                description = "Какой метод НЕ генерируется автоматически для data class?",
                answerOptions = listOf("equals()", "hashCode()", "compareTo()", "copy()"),
                correctOption = 3,
            ),
            QuestionDto.Choice(
                id = "lateinit-limits",
                theme = "Типы данных",
                difficultyLevel = 3,
                description = "С каким типом свойства нельзя использовать lateinit?",
                answerOptions = listOf("String", "Int", "List<String>", "MutableMap<String, Int>"),
                correctOption = 2,
            ),

            // ───── Уровень 4 ─────
            QuestionDto.Choice(
                id = "sealed-vs-enum",
                theme = "ООП",
                difficultyLevel = 4,
                description = "В чём главное преимущество sealed interface перед enum?",
                answerOptions = listOf(
                    "Он быстрее работает",
                    "Наследники могут хранить собственные данные разных типов",
                    "Он не требует when",
                    "Его можно наследовать из другого модуля",
                ),
                correctOption = 2,
            ),
            QuestionDto.Open(
                id = "inline-keyword",
                theme = "Функции",
                difficultyLevel = 4,
                description = "Какое ключевое слово позволяет использовать reified-параметр типа?",
                correctText = "inline",
            ),
            QuestionDto.Matching(
                id = "variance-modifiers",
                theme = "Типы данных",
                difficultyLevel = 4,
                description = "Сопоставьте модификатор с его ролью",
                list1 = listOf("out", "in", "reified", "vararg"),
                list2 = listOf(
                    "ковариантность: тип только возвращается",
                    "контравариантность: тип только принимается",
                    "тип доступен в runtime",
                    "переменное число аргументов",
                ),
                pairs = listOf(PairDto(0, 0), PairDto(1, 1), PairDto(2, 2), PairDto(3, 3)),
            ),

            // ───── Уровень 5 ─────
            QuestionDto.Choice(
                id = "coroutine-suspend",
                theme = "Функции",
                difficultyLevel = 5,
                description = "Что на самом деле делает компилятор с suspend-функцией?",
                answerOptions = listOf(
                    "Оборачивает её в отдельный поток",
                    "Превращает в конечный автомат и добавляет параметр Continuation",
                    "Помечает её как synchronized",
                    "Компилирует в JavaScript",
                ),
                correctOption = 2,
            ),
            QuestionDto.Choice(
                id = "delegated-properties",
                theme = "ООП",
                difficultyLevel = 5,
                description = "Какие операторы должен предоставлять делегат для var-свойства?",
                answerOptions = listOf(
                    "только getValue",
                    "getValue и setValue",
                    "invoke и getValue",
                    "provideDelegate и invoke",
                ),
                correctOption = 2,
            ),
            QuestionDto.Open(
                id = "nothing-type",
                theme = "Типы данных",
                difficultyLevel = 5,
                description = "Какой тип в Kotlin является подтипом всех типов и не имеет ни одного значения?",
                correctText = "Nothing",
            ),
            // ← сюда переносите остальные вопросы из прежнего QuestionRepository.default()
        ),
    )
}