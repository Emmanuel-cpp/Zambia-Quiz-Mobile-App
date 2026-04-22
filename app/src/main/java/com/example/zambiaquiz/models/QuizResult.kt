package com.example.zambiaquiz.models
data class QuizResult(
    val totalQuestions: Int,
    val correctAnswers: Int,
    val wrongAnswers: Int,
    val skippedQuestions: Int,
    val score: Int,
    val percentage: Float,
    val timeTaken: Long,
    val category: String
)