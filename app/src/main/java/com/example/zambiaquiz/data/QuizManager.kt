package com.example.zambiaquiz.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.example.zambiaquiz.models.Question
import com.example.zambiaquiz.models.QuizCategory
import java.io.IOException
class QuizManager(private val context: Context) {

    private var allQuestions: List<Question> = emptyList()

    init {
        loadQuestions()
        Log.d("QuizManager", "Loaded ${allQuestions.size} total questions")
    }

    private fun loadQuestions() {
        try {
            val jsonString = context.assets.open("questions.json")
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<List<Question>>() {}.type
            allQuestions = Gson().fromJson(jsonString, type)

        } catch (e: IOException) {
            Log.e("QuizManager", "Error loading questions", e)
            e.printStackTrace()
        }
    }

    fun getCategories(): List<QuizCategory> {
        val allowedCategories = listOf(
            "History",
            "Geography",
            "Culture & Traditions",
            "Wildlife & Nature",
            "Economy & Resources",
            "Sports",
            "Politics & Government",
            "Famous People"
        )

        val categories = allQuestions
            .filter { allowedCategories.contains(it.category) }
            .groupBy { it.category }

        return allowedCategories.mapNotNull { categoryName ->
            categories[categoryName]?.let { questions ->
                QuizCategory(
                    id = categoryName.lowercase().replace(" ", "_").replace("&", "and"),
                    name = categoryName,
                    description = "Test your knowledge about $categoryName",
                    icon = getCategoryIcon(categoryName),
                    questionCount = questions.size
                )
            }
        }
    }

    private fun getCategoryIcon(category: String): String {
        return when (category.lowercase()) {
            "history" -> "🏛️"
            "geography" -> "🗺️"
            "culture & traditions" -> "🎭"
            "wildlife & nature" -> "🦁"
            "economy & resources" -> "💎"
            "sports" -> "⚽"
            "politics & government" -> "⚖️"
            "famous people" -> "👤"
            else -> "📚"
        }
    }

    fun getQuestionsByCategory(category: String, count: Int = 10, difficulty: String = "All"): List<Question> {
        var filtered = allQuestions.filter { it.category.equals(category, ignoreCase = true) }

        if (difficulty != "All") {
            filtered = filtered.filter { it.difficulty.equals(difficulty, ignoreCase = true) }
        }

        return filtered
            .shuffled()
            .take(count)
            .map { it.shuffled() }
    }

    fun getRandomQuestions(count: Int = 10): List<Question> {
        return allQuestions
            .shuffled()
            .take(count)
            .map { it.shuffled() }
    }

    fun getTotalQuestions(): Int = allQuestions.size
}