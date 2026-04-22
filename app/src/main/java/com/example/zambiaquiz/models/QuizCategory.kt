package com.example.zambiaquiz.models

data class QuizCategory(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val questionCount: Int
)