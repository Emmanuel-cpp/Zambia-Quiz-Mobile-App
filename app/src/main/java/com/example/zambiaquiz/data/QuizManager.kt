package com.example.zambiaquiz.data

import android.content.Context
import android.util.Log
import com.example.zambiaquiz.models.Question
import com.example.zambiaquiz.models.QuizCategory
import com.google.gson.Gson
import com.google.gson.JsonParser
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class QuizManager private constructor(private val context: Context) {

    private var allQuestions: List<Question> = emptyList()
    private var isLoadedFromServer = false
    private var hasLoaded = false

    companion object {
        @Volatile
        private var INSTANCE: QuizManager? = null

        fun getInstance(context: Context): QuizManager {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: QuizManager(context.applicationContext).also { INSTANCE = it }
            }
        }
    }

    suspend fun loadQuestions(forceReload: Boolean = false): Boolean {
        // ⭐ Don't reload if already loaded (unless forced)
        if (hasLoaded && !forceReload && allQuestions.isNotEmpty()) {
            Log.d("QuizManager", "✅ Questions already loaded: ${allQuestions.size}")
            return true
        }

        return withContext(Dispatchers.IO) {
            try {
                Log.d("QuizManager", "🌐 Loading from: https://sangwapo.com/api/questions.json")
                val response = RetrofitClient.apiService.getQuestions()

                if (response.isSuccessful && response.body() != null) {
                    val jsonString = response.body()!!.string()
                    allQuestions = parseQuestionsManually(jsonString)

                    if (allQuestions.isNotEmpty()) {
                        isLoadedFromServer = true
                        hasLoaded = true
                        Log.d("QuizManager", "✅ SUCCESS: Loaded ${allQuestions.size} questions from SERVER")
                        true
                    } else {
                        Log.e("QuizManager", "No valid questions found from server")
                        loadQuestionsFromAssets()
                    }
                } else {
                    Log.e("QuizManager", "Server error: ${response.code()} - ${response.message()}")
                    loadQuestionsFromAssets()
                }
            } catch (e: Exception) {
                Log.e("QuizManager", "Network error: ${e.message}")
                e.printStackTrace()
                loadQuestionsFromAssets()
            }
        }
    }

    private fun parseQuestionsManually(jsonString: String): List<Question> {
        val questions = mutableListOf<Question>()
        var skippedCount = 0

        try {
            val jsonArray = JsonParser.parseString(jsonString).asJsonArray
            Log.d("QuizManager", "Found ${jsonArray.size()} questions in JSON")

            jsonArray.forEachIndexed { index, element ->
                try {
                    val obj = element.asJsonObject

                    val questionText = obj.get("Question")?.asString
                        ?: obj.get("question")?.asString
                        ?: ""

                    val optionsList = mutableListOf<String>()
                    val optionsElement = obj.get("Options") ?: obj.get("options")

                    when {
                        optionsElement == null -> {}
                        optionsElement.isJsonArray -> {
                            optionsElement.asJsonArray.forEach {
                                optionsList.add(it.asString)
                            }
                        }
                        optionsElement.isJsonObject -> {
                            val optionsObj = optionsElement.asJsonObject
                            optionsObj.keySet().sorted().forEach { key ->
                                optionsList.add(optionsObj.get(key).asString)
                            }
                        }
                    }

                    val answer = obj.get("Answer")?.asString
                        ?: obj.get("answer")?.asString
                        ?: ""

                    val category = obj.get("Category")?.asString
                        ?: obj.get("category")?.asString
                        ?: "General"

                    val difficulty = obj.get("Difficulty")?.asString
                        ?: obj.get("difficulty")?.asString
                        ?: "Medium"

                    val id = obj.get("Number")?.asInt
                        ?: obj.get("id")?.asInt
                        ?: (index + 1)

                    if (questionText.isNotEmpty() && optionsList.size >= 2 && answer.isNotEmpty()) {
                        questions.add(
                            Question(
                                id = id,
                                question = questionText,
                                options = optionsList,
                                answer = answer,
                                category = category,
                                difficulty = difficulty
                            )
                        )
                    } else {
                        skippedCount++
                    }
                } catch (e: Exception) {
                    skippedCount++
                }
            }

            Log.d("QuizManager", "✅ Parsed: ${questions.size} questions, Skipped: $skippedCount")

        } catch (e: Exception) {
            Log.e("QuizManager", "Failed to parse JSON: ${e.message}")
            e.printStackTrace()
        }

        return questions
    }

    private fun loadQuestionsFromAssets(): Boolean {
        return try {
            Log.d("QuizManager", "📁 Falling back to local assets/questions.json")
            val json = context.assets.open("questions.json")
                .bufferedReader()
                .use { it.readText() }

            val type = object : TypeToken<List<Question>>() {}.type
            allQuestions = Gson().fromJson(json, type)
            isLoadedFromServer = false
            hasLoaded = true
            Log.d("QuizManager", "✅ Loaded ${allQuestions.size} questions from LOCAL assets")
            true
        } catch (e: Exception) {
            Log.e("QuizManager", "Failed to load from assets: ${e.message}")
            e.printStackTrace()
            false
        }
    }

    fun getQuestionsByCategory(category: String, count: Int, difficulty: String): List<Question> {
        var filtered = allQuestions.filter {
            it.category.equals(category, ignoreCase = true)
        }

        if (difficulty != "All") {
            filtered = filtered.filter {
                it.difficulty.equals(difficulty, ignoreCase = true)
            }
        }

        return filtered.shuffled().take(count).map { it.shuffled() }
    }

    fun getRandomQuestions(count: Int): List<Question> {
        return allQuestions.shuffled().take(count).map { it.shuffled() }
    }

    fun getCategories(): List<QuizCategory> {
        val uniqueCategories = allQuestions
            .map { it.category }
            .distinct()
            .filter { it.isNotEmpty() }

        return uniqueCategories.map { categoryName ->
            val count = allQuestions.count {
                it.category.equals(categoryName, ignoreCase = true)
            }
            QuizCategory(
                name = categoryName,
                icon = getCategoryIcon(categoryName),
                questionCount = count
            )
        }
    }

    fun getTotalQuestions(): Int = allQuestions.size

    fun isFromServer(): Boolean = isLoadedFromServer

    fun isLoaded(): Boolean = hasLoaded && allQuestions.isNotEmpty()

    private fun getCategoryIcon(category: String): String {
        return when {
            category.contains("History", ignoreCase = true) -> "📜"
            category.contains("Geography", ignoreCase = true) -> "🗺️"
            category.contains("Environment", ignoreCase = true) -> "🌍"
            category.contains("Culture", ignoreCase = true) -> "🎭"
            category.contains("Tradition", ignoreCase = true) -> "🎭"
            category.contains("Wildlife", ignoreCase = true) -> "🦁"
            category.contains("Nature", ignoreCase = true) -> "🌳"
            category.contains("Economy", ignoreCase = true) -> "💰"
            category.contains("Economics", ignoreCase = true) -> "💰"
            category.contains("Resources", ignoreCase = true) -> "⛏️"
            category.contains("Sports", ignoreCase = true) -> "⚽"
            category.contains("Politics", ignoreCase = true) -> "🏛️"
            category.contains("Government", ignoreCase = true) -> "🏛️"
            category.contains("Civics", ignoreCase = true) -> "🏛️"
            category.contains("People", ignoreCase = true) -> "⭐"
            category.contains("Famous", ignoreCase = true) -> "⭐"
            category.contains("General", ignoreCase = true) -> "📖"
            category.contains("Knowledge", ignoreCase = true) -> "🧠"
            category.contains("Music", ignoreCase = true) -> "🎵"
            category.contains("Food", ignoreCase = true) -> "🍲"
            category.contains("Language", ignoreCase = true) -> "🗣️"
            category.contains("Religion", ignoreCase = true) -> "⛪"
            category.contains("Education", ignoreCase = true) -> "📚"
            category.contains("Science", ignoreCase = true) -> "🔬"
            category.contains("Technology", ignoreCase = true) -> "💻"
            else -> "📚"
        }
    }
}