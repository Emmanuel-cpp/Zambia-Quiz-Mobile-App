package com.example.zambiaquiz.ui

import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.viewpager2.widget.ViewPager2
import com.example.zambiaquiz.R
import com.example.zambiaquiz.data.QuizManager
import com.example.zambiaquiz.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var quizManager: QuizManager
    private var mediaPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizManager = QuizManager(this)
        setupUI()
        setupImageSlider()
        setupClickListeners()
    }

    private fun setupUI() {
        val slideUp = AnimationUtils.loadAnimation(this, R.anim.slide_up)
        binding.mainContainer.startAnimation(slideUp)

        val totalQuestions = quizManager.getTotalQuestions()
        binding.tvTotalQuestions.text = "Over $totalQuestions questions about Zambia!"
    }

    private fun setupImageSlider() {
        val slides = listOf(
            SlideItem(R.drawable.zambia_1),
            SlideItem(R.drawable.zambia_2),
            SlideItem(R.drawable.zambia_3),
            SlideItem(R.drawable.zambia_4),
            SlideItem(R.drawable.zambia_5)
        )

        binding.imageSlider.adapter = ImageSliderAdapter(slides)

        // Make transitions slower and smoother
        binding.imageSlider.offscreenPageLimit = 2

        // smooth page transformer with slow fade and slide
        binding.imageSlider.setPageTransformer { page, position ->
            page.apply {
                when {
                    position < -1 -> { // Pages way off to the left
                        alpha = 0f
                    }
                    position <= 1 -> { // Pages transitioning
                        // Fade effect
                        alpha = 1 - kotlin.math.abs(position) * 0.5f

                        // Slide effect
                        translationX = -position * width * 0.3f

                        // zoom effect
                        val scaleFactor = 1 - kotlin.math.abs(position) * 0.05f
                        scaleX = scaleFactor
                        scaleY = scaleFactor
                    }
                    else -> { // Pages way off to the right
                        alpha = 0f
                    }
                }
            }
        }

        // Update dots when page changes
        binding.imageSlider.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateDots(position)
            }
        })

        // Auto-scroll every 6 seconds (slower for smooth viewing)
        val handler = Handler(Looper.getMainLooper())
        val runnable = object : Runnable {
            override fun run() {
                val currentItem = binding.imageSlider.currentItem
                val nextItem = (currentItem + 1) % slides.size
                // Smooth transition duration
                binding.imageSlider.setCurrentItem(nextItem, true)
                handler.postDelayed(this, 6000)
            }
        }
        handler.postDelayed(runnable, 6000)
    }

    private fun updateDots(position: Int) {
        binding.dot1.setBackgroundResource(if (position == 0) R.drawable.dot_active else R.drawable.dot_inactive)
        binding.dot2.setBackgroundResource(if (position == 1) R.drawable.dot_active else R.drawable.dot_inactive)
        binding.dot3.setBackgroundResource(if (position == 2) R.drawable.dot_active else R.drawable.dot_inactive)
        binding.dot4.setBackgroundResource(if (position == 3) R.drawable.dot_active else R.drawable.dot_inactive)
        binding.dot5.setBackgroundResource(if (position == 4) R.drawable.dot_active else R.drawable.dot_inactive)
    }

    private fun setupClickListeners() {
        binding.btnStartQuiz.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce))
            startActivity(Intent(this, CategoryActivity::class.java))
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnQuickPlay.setOnClickListener {
            it.startAnimation(AnimationUtils.loadAnimation(this, R.anim.bounce))
            val intent = Intent(this, QuizActivity::class.java).apply {
                putExtra("CATEGORY", "Random")
                putExtra("DIFFICULTY", "All")
                putExtra("QUESTION_COUNT", 10)
            }
            startActivity(intent)
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
        }

        binding.btnAbout.setOnClickListener {
            showAboutDialog()
        }
    }

    private fun showAboutDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_about, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogView.findViewById<View>(R.id.btnClose).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun playBackgroundMusic() {
        try {
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    override fun onPause() {
        super.onPause()
        mediaPlayer?.pause()
    }
    override fun onResume() {
        super.onResume()
        mediaPlayer?.start()
    }
    override fun onDestroy() {
        super.onDestroy()
        mediaPlayer?.release()
        mediaPlayer = null
    }
}