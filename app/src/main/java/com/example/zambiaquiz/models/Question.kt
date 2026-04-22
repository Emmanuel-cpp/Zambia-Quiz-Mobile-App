package com.example.zambiaquiz.models

data class Question(
    val id: Int = 0,
    val category: String,
    val difficulty: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val explanation: String = ""
) {
    fun shuffled(): Question {
        val correctAnswerText = options[correctAnswer]
        val shuffledOptions = options.shuffled()
        val newCorrectIndex = shuffledOptions.indexOf(correctAnswerText)

        return this.copy(
            options = shuffledOptions,
            correctAnswer = newCorrectIndex
        )
    }
}