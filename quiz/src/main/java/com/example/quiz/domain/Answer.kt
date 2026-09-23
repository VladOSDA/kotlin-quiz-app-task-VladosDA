package com.example.quiz.domain

sealed interface Answer {

    data class Choice(val index: Int) : Answer
    data class Text(val value: String) : Answer
    data class Matching(val pairs: Map<Int, Int>) : Answer
}