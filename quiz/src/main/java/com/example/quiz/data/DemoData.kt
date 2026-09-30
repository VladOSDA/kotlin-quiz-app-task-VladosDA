package com.example.quiz.data

import com.example.quiz.data.dto.QuestionBankDto
import com.example.quiz.data.dto.QuestionsDto

object DemoData {

    fun questionBank(): QuestionBankDto = QuestionBankDto(
        themes = listOf(
            "Синтаксис",
            "Безопасность null",
            "Коллекции",
            "Типы данных",
            "Функции",
            "ООП",
            "Физика",
        ),
        questions = listOf(

            // ───────── Синтаксис ─────────
            QuestionsDto.Choice(
                id = "val-vs-var",
                theme = "Синтаксис",
                difficultyLevel = 0,
                description = "Какой синтаксис объявляет неизменяемую (read-only) переменную?",
                answerOptions = listOf("var x = 5", "val x = 5", "let x = 5", "final x = 5"),
                correctAnswer = 2,
            ),
            QuestionsDto.Open(
                id = "entry-point",
                theme = "Синтаксис",
                difficultyLevel = 0,
                description = "Как называется функция — точка входа в Kotlin-программу?",
                correctAnswer = "main",
            ),
            QuestionsDto.Open(
                id = "string-template",
                theme = "Синтаксис",
                difficultyLevel = 1,
                description = "Какой символ начинает подстановку значения в строковый шаблон?",
                correctAnswer = "$",
            ),
            QuestionsDto.Choice(
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
                correctAnswer = 2,
            ),

            // ───────── Безопасность null ─────────
            QuestionsDto.Choice(
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
                correctAnswer = 1,
            ),
            QuestionsDto.Choice(
                id = "elvis",
                theme = "Безопасность null",
                difficultyLevel = 1,
                description = "В каком случае выражение x ?: 5 вернёт именно 5?",
                answerOptions = listOf("Всегда", "Если x == null", "Если x == 0", "Никогда"),
                correctAnswer = 2,
            ),

            // ───────── Коллекции ─────────
            QuestionsDto.Choice(
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
                correctAnswer = 1,
            ),
            QuestionsDto.Open(
                id = "associate-by",
                theme = "Коллекции",
                difficultyLevel = 2,
                description = "Какая функция превращает List в Map, принимая лямбду-селектор ключа? (одно слово)",
                correctAnswer = "associateBy",
            ),

            // ───────── Типы данных ─────────
            QuestionsDto.Choice(
                id = "int-division",
                theme = "Типы данных",
                difficultyLevel = 0,
                description = "Что вернёт выражение 10 / 3, если оба операнда типа Int?",
                answerOptions = listOf("3", "3.33", "3.0", "Ошибка компиляции"),
                correctAnswer = 1,
            ),
            QuestionsDto.Choice(
                id = "lateinit-limits",
                theme = "Типы данных",
                difficultyLevel = 3,
                description = "С каким типом свойства нельзя использовать lateinit?",
                answerOptions = listOf("String", "Int", "List<String>", "MutableMap<String, Int>"),
                correctAnswer = 2,
            ),
            QuestionsDto.Matching(
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
                correctAnswer = mapOf(0 to 0,
                    1 to 1,
                    2 to 2,
                    3 to 3,),
            ),
            QuestionsDto.Open(
                id = "nothing-type",
                theme = "Типы данных",
                difficultyLevel = 5,
                description = "Какой тип в Kotlin является подтипом всех типов и не имеет ни одного значения?",
                correctAnswer = "Nothing",
            ),

            // ───────── Функции ─────────
            QuestionsDto.Matching(
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
                correctAnswer = mapOf(0 to 0,
                    1 to 1,
                    2 to 2,
                    3 to 3,),
            ),
            QuestionsDto.Open(
                id = "inline-keyword",
                theme = "Функции",
                difficultyLevel = 4,
                description = "Какое ключевое слово позволяет использовать reified-параметр типа?",
                correctAnswer = "inline",
            ),
            QuestionsDto.Choice(
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
                correctAnswer = 2,
            ),

            // ───────── ООП ─────────
            QuestionsDto.Choice(
                id = "data-class-generated",
                theme = "ООП",
                difficultyLevel = 3,
                description = "Какой метод НЕ генерируется автоматически для data class?",
                answerOptions = listOf("equals()", "hashCode()", "compareTo()", "copy()"),
                correctAnswer = 3,
            ),
            QuestionsDto.Choice(
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
                correctAnswer = 2,
            ),
            QuestionsDto.Choice(
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
                correctAnswer = 2,
            ),

            // ───────── Физика ─────────
            QuestionsDto.Choice(
                id = "NewtonLaws",
                theme = "Физика",
                difficultyLevel = 1,
                description = "Сколько законов придумал Ньютон?",
                answerOptions = listOf("1", "2", "3", "4", "5", "6"),
                correctAnswer = 3,
            ),
            QuestionsDto.Open(
                id = "NewtonLaw",
                theme = "Физика",
                difficultyLevel = 2,
                description = "Выпишите уравнение 2 закона Ньютона",
                correctAnswer = "F=ma",
            ),
            QuestionsDto.Matching(
                id = "Newton",
                theme = "Физика",
                difficultyLevel = 3,
                description = "Сопоставьте формулу и закон Ньютона",
                list1 = listOf("F=ma", "v=0", "F1+F2=0"),
                list2 = listOf("1 Закон", "2 Закон", "3 Закон"),
                correctAnswer = mapOf(0 to 1,
                    1 to 0,
                    2 to 2),
            ),
        ),
    )
}