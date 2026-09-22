package com.example.quiz.domain

object AnswerFormatter {

    fun studentAnswer(question: QuestionType, answer: Answer): String = when {
        question is MultipleChoice && answer is Answer.Choice ->
            "${answer.index}) ${question.answerOptions.getOrElse(answer.index - 1) { "?" }}"

        question is OpenQuestion && answer is Answer.Text ->
            answer.value

        question is MapOptions && answer is Answer.Matching ->
            formatPairs(question, answer.pairs)

        else -> "—"
    }

    fun correctAnswer(question: QuestionType): String = when (question) {
        is MultipleChoice ->
            "${question.correctAnswer}) ${question.answerOptions[question.correctAnswer - 1]}"

        is OpenQuestion ->
            question.correctAnswer

        is MapOptions ->
            formatPairs(question, question.correctAnswer)
    }

    private fun formatPairs(question: MapOptions, pairs: Map<Int, Int>): String =
        pairs.entries.sortedBy { it.key }.joinToString("; ") { (left, right) ->
            "${question.list1.getOrElse(left) { "?" }} — ${question.list2.getOrElse(right) { "?" }}"
        }
}