package com.example.quiz.domain

data class QuestionTheme(val themeName:String){
    init {
        require(themeName.isNotBlank()) { "Название темы не может быть пустым!" }
    }
}