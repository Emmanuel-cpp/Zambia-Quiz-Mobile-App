package com.example.zambiaquiz.models

import com.google.gson.annotations.SerializedName

data class Question(
    @SerializedName(value = "id", alternate = ["Number"])
    val id: Int = 0,

    @SerializedName(value = "question", alternate = ["Question"])
    val question: String = "",

    @SerializedName(value = "options", alternate = ["Options"])
    val options: List<String> = emptyList(),

    @SerializedName(value = "correctAnswer")
    val correctAnswer: Int = 0,

    @SerializedName(value = "answer", alternate = ["Answer"])
    val answer: String = "",

    @SerializedName(value = "category", alternate = ["Category"])
    val category: String = "General",

    @SerializedName(value = "difficulty", alternate = ["Difficulty"])
    val difficulty: String = "Medium",

    @SerializedName(value = "description")
    val description: String = ""
) {
    // Get the correct answer index
    fun getCorrectIndex(): Int {
        return if (answer.isNotEmpty()) {
            // Server format: find index of answer text in options
            val index = options.indexOf(answer)
            if (index >= 0) index else 0
        } else {
            // Local format: use correctAnswer integer
            correctAnswer
        }
    }

    fun shuffled(): Question {
        val correctIndex = getCorrectIndex()
        val correctAnswerText = options.getOrNull(correctIndex) ?: return this
        val shuffledOptions = options.shuffled()
        val newCorrectIndex = shuffledOptions.indexOf(correctAnswerText)

        return this.copy(
            options = shuffledOptions,
            correctAnswer = newCorrectIndex,
            answer = correctAnswerText
        )
    }
}