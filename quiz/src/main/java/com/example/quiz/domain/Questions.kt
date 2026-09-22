package com.example.quiz.domain

const val MIN_DIFFICULTY_LEVEL = 0
const val MAX_DIFFICULTY_LEVEL = 5

internal fun requireValidDifficulty(level: Int, description: String) {
    require(level in MIN_DIFFICULTY_LEVEL..MAX_DIFFICULTY_LEVEL) {
        "У задания <<$description>> уровень сложности вне диапазона $MIN_DIFFICULTY_LEVEL..$MAX_DIFFICULTY_LEVEL"
    }
}

sealed interface Answer {

    data class Choice(val index: Int) : Answer
    data class Text(val value: String) : Answer
    data class Matching(val pairs: Map<Int, Int>) : Answer
}

sealed interface QuestionType{
    val theme: QuestionTheme

    val id: String
    val difficultyLevel: Int
    val description: String
    fun checkCorrect(answer: Answer): Boolean
}

data class MultipleChoice(
    override val id: String,
    override val theme: QuestionTheme,
    override val difficultyLevel: Int,
    override val description: String,
    val correctAnswer: Int,
    val answerOptions: List<String>

): QuestionType{

    init {
        require(description.isNotEmpty()){ "Описане должно быть не пустым!" }
        require(answerOptions.size > 1){ "В задании <<$description>> меньше 2 ответов!"}
        require(correctAnswer in 1..answerOptions.size){
            "В задании <<$description>> correctAnswer = $correctAnswer, а вариантов всего ${answerOptions.size}" }
        requireValidDifficulty(difficultyLevel, description)
    }

    override fun checkCorrect(answer: Answer): Boolean {
        val numberOfOptions = answerOptions.size
        require(answer is Answer.Choice) {"Для <<$description>> нужен ответ типа Choice"}

        require(answer.index in 1..numberOfOptions) {"Ответ должен быть вариантом из списка!"}

        return answer.index == correctAnswer
    }

}

data class OpenQuestion(
    override val id: String,
    override val theme: QuestionTheme,
    override val difficultyLevel: Int,
    override val description: String,
    val correctAnswer: String

): QuestionType{

    init {
        require(description.isNotEmpty()){ "Описане должно быть не пустым!" }
        require(correctAnswer.isNotEmpty()){
            "В задании <<$description>> пустой правильный ответ!" }
        requireValidDifficulty(difficultyLevel, description)
    }

    override fun checkCorrect(answer: Answer): Boolean {

        require(answer is Answer.Text){"Для <<$description>> нужен ответ типа Text"}
        require (answer.value.isNotEmpty()) {"Ответ должен быть не пустым!"}
        require(answer.value.trim().isNotEmpty()) {"Ответ должен состоять не только из пробелов!"}
        return (answer.value.trim().equals(correctAnswer.trim(), ignoreCase = true))
    }

}

data class MapOptions(
    override val id: String,
    override val theme: QuestionTheme,
    override val difficultyLevel: Int,
    override val description: String,
    val correctAnswer: Map<Int, Int>,
    val list1: List<String>,
    val list2: List<String>

): QuestionType{

    init {
        require(description.isNotEmpty()){ "Описане должно быть не пустым!" }
        require(list1.size ==list2.size){"В задании <<$description>> разное количество элементов на сопоставление"}
        require(list1.size > 1){ "В задании <<$description>> меньше 2 ответов!"}
        require(correctAnswer.keys == list1.indices.toSet()){
            "В задании <<$description>> в правильном ответе keys не совпадает с индексами элементов!" }
        require(correctAnswer.values.toSet() == list2.indices.toSet()){
            "В задании <<$description>> в правильном ответе values не совпадает с индексами элементов!" }
        requireValidDifficulty(difficultyLevel, description)
    }


    override fun checkCorrect(answer: Answer): Boolean {

        require(answer is Answer.Matching){"Для <<$description>> нужен ответ типа Matching"}

        require(answer.pairs.keys == correctAnswer.keys &&
            answer.pairs.values.toSet() == correctAnswer.values.toSet()) {
            "Элементы должны соответствовать заданию!"
        }

        return (answer.pairs == correctAnswer)

    }
}


data class AnsweredQuestion(
    val questionId: String,
    val answer: Answer,
    val isCorrect: Boolean
)