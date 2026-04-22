package com.example.zambiaquiz.ui

import android.animation.ObjectAnimator
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.viewpager2.widget.ViewPager2
import com.example.zambiaquiz.R
import com.example.zambiaquiz.data.QuizManager
import com.example.zambiaquiz.databinding.ActivityQuizBinding
import com.example.zambiaquiz.models.Question

class QuizActivity : AppCompatActivity() {

    private lateinit var binding: ActivityQuizBinding
    private lateinit var quizManager: QuizManager
    private lateinit var questions: List<Question>

    private var currentQuestionIndex = 0
    private var correctAnswers = 0
    private var wrongAnswers = 0
    private var skippedQuestions = 0
    private var startTime = 0L

    private var timer: CountDownTimer? = null
    private val questionTimeLimit = 20000L

    private lateinit var vibrator: Vibrator
    private var correctSound: MediaPlayer? = null
    private var wrongSound: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityQuizBinding.inflate(layoutInflater)
        setContentView(binding.root)

        @Suppress("DEPRECATION")
        vibrator = getSystemService(VIBRATOR_SERVICE) as Vibrator
        quizManager = QuizManager(this)

        initializeSounds()

        loadQuestions()
        setupUI()
        setupBackgroundSlider()
        startTime = System.currentTimeMillis()
        displayQuestion()
    }

    private fun initializeSounds() {
        try {
            correctSound = MediaPlayer.create(this, R.raw.correct_answer)
            wrongSound = MediaPlayer.create(this, R.raw.wrong_answer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

private fun loadQuestions() {
    val category = intent.getStringExtra("CATEGORY") ?: "Random"
    val difficulty = intent.getStringExtra("DIFFICULTY") ?: "All"
    val count = intent.getIntExtra("QUESTION_COUNT", 10)

    Log.d("QuizActivity", " Loading quiz: Category=$category, Difficulty=$difficulty, Count=$count")

    questions = if (category == "Random") {
        quizManager.getRandomQuestions(count)
    } else {
        quizManager.getQuestionsByCategory(category, count, difficulty)
    }

    if (questions.isEmpty()) {
        showNoQuestionsDialog()
    }
}
    private fun setupUI() {
        supportActionBar?.hide()

        binding.btnOption1.setOnClickListener { checkAnswer(0) }
        binding.btnOption2.setOnClickListener { checkAnswer(1) }
        binding.btnOption3.setOnClickListener { checkAnswer(2) }
        binding.btnOption4.setOnClickListener { checkAnswer(3) }
        binding.btnSkip.setOnClickListener { skipQuestion() }
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

    private fun displayQuestion() {
        if (currentQuestionIndex >= questions.size) {
            finishQuiz()
            return
        }

        val question = questions[currentQuestionIndex]

        binding.tvProgress.text = "${currentQuestionIndex + 1}/${questions.size}"
        binding.progressBar.max = questions.size
        binding.progressBar.progress = currentQuestionIndex + 1

        // Smooth fade and slide animation
        binding.tvQuestion.alpha = 0f
        binding.tvQuestion.translationY = 30f
        binding.tvQuestion.text = question.question
        binding.tvQuestion.animate()
            .alpha(1f)
            .translationY(0f)
            .setDuration(400)
            .setInterpolator(android.view.animation.DecelerateInterpolator())
            .start()

        val options = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)
        question.options.forEachIndexed { index, option ->
            if (index < options.size) {
                options[index].text = option
                options[index].isEnabled = true
                options[index].setBackgroundColor(ContextCompat.getColor(this, R.color.option_default))
                options[index].setTextColor(ContextCompat.getColor(this, R.color.text_primary))
            }
        }

        startQuestionTimer()
    }

    private fun startQuestionTimer() {
        timer?.cancel()
        binding.tvTimer.visibility = View.VISIBLE

        timer = object : CountDownTimer(questionTimeLimit, 100) {
            override fun onTick(millisRemaining: Long) {
                val secondsRemaining = millisRemaining / 1000
                binding.tvTimer.text = "⏱️ ${secondsRemaining}s"

                if (secondsRemaining <= 5) {
                    binding.tvTimer.setTextColor(ContextCompat.getColor(this@QuizActivity, R.color.error))
                } else {
                    binding.tvTimer.setTextColor(ContextCompat.getColor(this@QuizActivity, android.R.color.white))
                }

                val progress = ((questionTimeLimit - millisRemaining).toFloat() / questionTimeLimit * 100).toInt()
                binding.timerProgress.progress = progress
            }

            override fun onFinish() {
                binding.tvTimer.text = "⏱️ 0s"
                skipQuestion()
            }
        }.start()
    }

    private fun checkAnswer(selectedIndex: Int) {
        timer?.cancel()

        val question = questions[currentQuestionIndex]
        val isCorrect = selectedIndex == question.correctAnswer

        val options = listOf(binding.btnOption1, binding.btnOption2, binding.btnOption3, binding.btnOption4)

        options.forEach { it.isEnabled = false }

        if (isCorrect) {
            correctAnswers++
            options[selectedIndex].setBackgroundColor(ContextCompat.getColor(this, R.color.correct))
            options[selectedIndex].setTextColor(ContextCompat.getColor(this, android.R.color.white))

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(100)
            }

            playSound(correctSound)

            val scale = ObjectAnimator.ofFloat(options[selectedIndex], "scaleX", 1.0f, 1.1f, 1.0f)
            scale.duration = 300
            scale.start()
        } else {
            wrongAnswers++
            options[selectedIndex].setBackgroundColor(ContextCompat.getColor(this, R.color.wrong))
            options[selectedIndex].setTextColor(ContextCompat.getColor(this, android.R.color.white))

            options[question.correctAnswer].setBackgroundColor(ContextCompat.getColor(this, R.color.correct))
            options[question.correctAnswer].setTextColor(ContextCompat.getColor(this, android.R.color.white))

            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                vibrator.vibrate(200)
            }

            playSound(wrongSound)
        }
        binding.root.postDelayed({
            currentQuestionIndex++
            displayQuestion()
        }, 1500)
    }
    private fun playSound(mediaPlayer: MediaPlayer?) {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.seekTo(0)
                } else {
                    it.start()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun skipQuestion() {
        timer?.cancel()
        skippedQuestions++
        currentQuestionIndex++
        displayQuestion()
    }
    private fun finishQuiz() {
        timer?.cancel()

        val timeTaken = System.currentTimeMillis() - startTime
        val score = (correctAnswers * 10)
        val percentage = (correctAnswers.toFloat() / questions.size * 100)

        val intent = Intent(this, ResultActivity::class.java).apply {
            putExtra("TOTAL_QUESTIONS", questions.size)
            putExtra("CORRECT_ANSWERS", correctAnswers)
            putExtra("WRONG_ANSWERS", wrongAnswers)
            putExtra("SKIPPED_QUESTIONS", skippedQuestions)
            putExtra("SCORE", score)
            putExtra("PERCENTAGE", percentage)
            putExtra("TIME_TAKEN", timeTaken)
            putExtra("CATEGORY", intent.getStringExtra("CATEGORY") ?: "Random")
        }

        startActivity(intent)
        finish()
    }

    private fun showNoQuestionsDialog() {
        AlertDialog.Builder(this)
            .setTitle("No Questions Available")
            .setMessage("Sorry, there are no questions available for this category and difficulty level.")
            .setPositiveButton("OK") { _, _ -> finish() }
            .setCancelable(false)
            .show()
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(this)
            .setTitle("Quit Quiz?")
            .setMessage("Are you sure you want to quit? Your progress will be lost.")
            .setPositiveButton("Quit") { _, _ ->
                @Suppress("DEPRECATION")
                super.onBackPressed()
            }
            .setNegativeButton("Continue", null)
            .show()
    }

    override fun onSupportNavigateUp(): Boolean {
        @Suppress("DEPRECATION")
        onBackPressed()
        return true
    }
    override fun onDestroy() {
        super.onDestroy()
        timer?.cancel()
        correctSound?.release()
        wrongSound?.release()
        correctSound = null
        wrongSound = null
    }
}