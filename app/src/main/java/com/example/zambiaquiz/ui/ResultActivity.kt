package com.example.zambiaquiz.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.zambiaquiz.R
import com.example.zambiaquiz.databinding.ActivityResultBinding
import kotlin.math.roundToInt

class ResultActivity : AppCompatActivity() {

    private lateinit var binding: ActivityResultBinding
    private var celebrationSound: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityResultBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.hide()

        setupBackgroundSlider()
        displayResults()
        setupClickListeners()
    }

    private fun setupBackgroundSlider() {
        val backgroundSlider = findViewById<ViewPager2>(R.id.backgroundSlider)
        val slides = listOf(
            SlideItem(R.drawable.zambia_1),
            SlideItem(R.drawable.zambia_2),
            SlideItem(R.drawable.zambia_3),
            SlideItem(R.drawable.zambia_4),
            SlideItem(R.drawable.zambia_5)
        )

        backgroundSlider.adapter = ImageSliderAdapter(slides)
        backgroundSlider.setPageTransformer { page, position ->
            page.alpha = 1 - kotlin.math.abs(position) * 0.5f
        }

        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val current = backgroundSlider.currentItem
                backgroundSlider.setCurrentItem((current + 1) % slides.size, true)
                handler.postDelayed(this, 6000)
            }
        }
        handler.postDelayed(runnable, 6000)
    }

    private fun displayResults() {
        val totalQuestions = intent.getIntExtra("TOTAL_QUESTIONS", 0)
        val correctAnswers = intent.getIntExtra("CORRECT_ANSWERS", 0)
        val wrongAnswers = intent.getIntExtra("WRONG_ANSWERS", 0)
        val skippedQuestions = intent.getIntExtra("SKIPPED_QUESTIONS", 0)
        val score = intent.getIntExtra("SCORE", 0)
        val percentage = intent.getFloatExtra("PERCENTAGE", 0f)
        val timeTaken = intent.getLongExtra("TIME_TAKEN", 0)
        val category = intent.getStringExtra("CATEGORY") ?: "Quiz"

        binding.tvCategory.text = category
        binding.tvScore.text = score.toString()
        binding.tvPercentage.text = "${percentage.roundToInt()}%"

        val message = when {
            percentage >= 90 -> {
                playCelebrationSound()
                "🏆 Outstanding!"
            }
            percentage >= 75 -> {
                playCelebrationSound()
                "⭐ Excellent!"
            }
            percentage >= 60 -> "👍 Good Job!"
            percentage >= 50 -> "😊 Not Bad!"
            else -> "Keep Practicing!"
        }

        binding.tvPerformanceMessage.text = message

        binding.tvTotalQuestions.text = totalQuestions.toString()
        binding.tvCorrectAnswers.text = correctAnswers.toString()
        binding.tvWrongAnswers.text = wrongAnswers.toString()
        binding.tvSkippedQuestions.text = skippedQuestions.toString()

        val minutes = timeTaken / 60000
        val seconds = (timeTaken % 60000) / 1000
        binding.tvTimeTaken.text = String.format("%02d:%02d", minutes, seconds)

        binding.circularProgress.apply {
            progress = 0
            max = 100
            postDelayed({
                progress = percentage.roundToInt()
            }, 500)
        }
    }

    private fun playCelebrationSound() {
        try {
            celebrationSound = MediaPlayer.create(this, R.raw.celebration)
            celebrationSound?.start()

            celebrationSound?.setOnCompletionListener {
                it.release()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun setupClickListeners() {
        binding.btnPlayAgain.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
            finish()
        }

        binding.btnHome.setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            })
            finish()
        }

        binding.btnShare.setOnClickListener {
            shareResults()
        }
    }

    private fun shareResults() {
        val percentage = intent.getFloatExtra("PERCENTAGE", 0f).roundToInt()
        val score = intent.getIntExtra("SCORE", 0)
        val category = intent.getStringExtra("CATEGORY") ?: "Quiz"

        val shareText = """
            I just scored $percentage% on the Zambia Quiz!
            
            Category: $category
            Score: $score points
            
            Test your knowledge about Zambia! 🇿🇲
        """.trimIndent()

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, shareText)
        }

        startActivity(Intent.createChooser(shareIntent, "Share your score"))
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        })
        finish()
    }

    override fun onDestroy() {
        super.onDestroy()
        celebrationSound?.release()
        celebrationSound = null
    }
}