package com.example.quiz.domain.questions

import com.example.quiz.domain.Answer
import com.example.quiz.domain.QuestionTheme

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