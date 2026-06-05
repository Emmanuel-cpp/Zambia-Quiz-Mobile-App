package com.example.zambiaquiz.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.zambiaquiz.R
import com.example.zambiaquiz.data.QuizManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private val minSplashDuration = 2500L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        // ⭐ Load questions during splash screen
        lifecycleScope.launch {
            val startTime = System.currentTimeMillis()

            Log.d("SplashActivity", "🚀 Starting to load questions...")

            // Get singleton instance and load questions
            val quizManager = QuizManager.getInstance(this@SplashActivity)
            val success = quizManager.loadQuestions()

            val loadTime = System.currentTimeMillis() - startTime
            Log.d("SplashActivity", "⏱️ Questions loaded in ${loadTime}ms - Success: $success")
            Log.d("SplashActivity", "📊 Total questions: ${quizManager.getTotalQuestions()}")
            Log.d("SplashActivity", "🌐 From server: ${quizManager.isFromServer()}")

            // Ensure splash is shown for at least 2.5 seconds (for branding)
            if (loadTime < minSplashDuration) {
                val remainingTime = minSplashDuration - loadTime
                Log.d("SplashActivity", "⏳ Waiting ${remainingTime}ms more for splash...")
                delay(remainingTime)
            }

            // Navigate to MainActivity
            Log.d("SplashActivity", "➡️ Navigating to MainActivity")
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
            finish()
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
        }
    }
}