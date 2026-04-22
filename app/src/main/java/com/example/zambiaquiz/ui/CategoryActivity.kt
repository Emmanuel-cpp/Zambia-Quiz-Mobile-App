package com.example.zambiaquiz.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.zambiaquiz.data.QuizManager
import com.example.zambiaquiz.models.QuizCategory
import com.example.zambiaquiz.R
import com.example.zambiaquiz.databinding.ActivityCategoryBinding
import android.os.Handler
import android.os.Looper
import androidx.viewpager2.widget.ViewPager2
import androidx.appcompat.app.AlertDialog


class CategoryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCategoryBinding
    private lateinit var quizManager: QuizManager
    private var selectedDifficulty = "All"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        quizManager = QuizManager(this)
        setupUI()
        setupBackgroundSlider()
        setupClickListeners()
    }

    private fun setupUI() {
        supportActionBar?.hide()

        val categories = quizManager.getCategories()
        binding.rvCategories.layoutManager = GridLayoutManager(this, 2)
        binding.rvCategories.adapter = CategoryAdapter(categories) { category ->
            showQuizOptions(category)
        }

        updateDifficultySelection()
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
            page.translationX = -position * page.width * 0.3f
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
    private fun setupClickListeners() {
        binding.btnDifficultyEasy.setOnClickListener {
            selectedDifficulty = "Easy"
            updateDifficultySelection()
        }

        binding.btnDifficultyMedium.setOnClickListener {
            selectedDifficulty = "Medium"
            updateDifficultySelection()
        }

        binding.btnDifficultyHard.setOnClickListener {
            selectedDifficulty = "Hard"
            updateDifficultySelection()
        }

        binding.btnDifficultyAll.setOnClickListener {
            selectedDifficulty = "All"
            updateDifficultySelection()
        }
    }

    private fun updateDifficultySelection() {
        binding.btnDifficultyEasy.alpha = 0.5f
        binding.btnDifficultyMedium.alpha = 0.5f
        binding.btnDifficultyHard.alpha = 0.5f
        binding.btnDifficultyAll.alpha = 0.5f

        when (selectedDifficulty) {
            "Easy" -> binding.btnDifficultyEasy.alpha = 1.0f
            "Medium" -> binding.btnDifficultyMedium.alpha = 1.0f
            "Hard" -> binding.btnDifficultyHard.alpha = 1.0f
            "All" -> binding.btnDifficultyAll.alpha = 1.0f
        }
    }

    private fun showQuizOptions(category: QuizCategory) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_quiz_options, binding.root, false)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        // Set category info
        dialogView.findViewById<TextView>(R.id.tvDialogTitle).text = category.name
        dialogView.findViewById<TextView>(R.id.tvDialogSubtitle).text = "${category.questionCount} questions available"

        // 10 Questions
        dialogView.findViewById<View>(R.id.btn10Questions).setOnClickListener {
            dialog.dismiss()
            startQuiz(category.name, 10)
        }

        // 20 Questions
        dialogView.findViewById<View>(R.id.btn20Questions).setOnClickListener {
            dialog.dismiss()
            startQuiz(category.name, 20)
        }

        // Cancel
        dialogView.findViewById<View>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun startQuiz(category: String, questionCount: Int) {
        val intent = Intent(this, QuizActivity::class.java).apply {
            putExtra("CATEGORY", category)
            putExtra("DIFFICULTY", selectedDifficulty)
            putExtra("QUESTION_COUNT", questionCount)
        }
        startActivity(intent)
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class CategoryAdapter(
        private val categories: List<QuizCategory>,
        private val onCategoryClick: (QuizCategory) -> Unit
    ) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

        class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
            val card: CardView = view.findViewById(R.id.categoryCard)
            val icon: TextView = view.findViewById(R.id.tvCategoryIcon)
            val name: TextView = view.findViewById(R.id.tvCategoryName)
            val count: TextView = view.findViewById(R.id.tvQuestionCount)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
            return CategoryViewHolder(view)
        }

        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            val category = categories[position]
            holder.icon.text = category.icon
            holder.name.text = category.name
            holder.count.text = "${category.questionCount} questions"
            holder.card.setOnClickListener {
                onCategoryClick(category)
            }
        }

        override fun getItemCount() = categories.size
    }
}