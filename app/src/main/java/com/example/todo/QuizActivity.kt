package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class QuizActivity : AppCompatActivity() {

    data class Question(val text: String, val options: List<String>, val correctIndex: Int)

    private lateinit var db: MyDBHelper
    private lateinit var questionText: TextView
    private lateinit var questionNumberText: TextView
    private lateinit var coinText: TextView
    private lateinit var radioGroup: RadioGroup
    private lateinit var optionButtons: List<RadioButton>
    private lateinit var timerText: TextView

    private val questions = listOf(
        Question("Jam I bere nga uji nese kthehem ne uji vdes.Cfare jam?", listOf("Uji", "Akull", "Zjarr"), 1),
        Question("Cfare shkon larte, kur shiu bie poshte?", listOf("Rete", "dielli", "cadra"), 2),
        Question("Çfarë është e thatë, por laget çdo herë pasi ta përdorësh?", listOf("Peshqiri", "Pjata", "Sfungjeri"), 2),
        Question("Une fluturoj por nuk kam krahe, qaj por nuk kam sy. Cfare jam?", listOf("Rete", "Ujevara", "Koha"), 0),
        Question("Sa me shume qe ben aq me shume prapa len. Cfare eshte?", listOf("Koha", "hapat", "rruga"), 1),
        Question("Jeni ne nje garevrapimi, dhe kaloni garuesin e dyte. Ne cilin ven dilni?", listOf("1", "2", "3"), 1),
        Question("Cfare eshte qe  te perket ty  por te tjeret e perdorin me shume se ti?", listOf("Telefoni", "emri", "datalindjes"), 1),
        Question("Nese me jep ushqim une jetoj por nese me jep uji une vdes. Cfare jam?", listOf("Zjarr", "uji", "genjeshter"), 0),
        Question("E humbi koken ne mengjes, por me kthehet prape ne mbremje? Cfare jam?", listOf("Kapelja", "Kepucet", "Jasteku"), 2)
    )

    private var currentIndex = 0
    private var coins = 0
    private var correctAnswers = 0
    private var incorrectAnswers = 0
    private var allowAnswer = true

    private lateinit var countDown: CountDownTimer

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quiz)

        db = MyDBHelper(this)

        questionText = findViewById(R.id.questionText)
        questionNumberText = findViewById(R.id.questionNumber)
        coinText = findViewById(R.id.coinsText)
        radioGroup = findViewById(R.id.radioGroup)
        timerText = findViewById(R.id.timerText)

        optionButtons = listOf(
            findViewById(R.id.option1),
            findViewById(R.id.option2),
            findViewById(R.id.option3)
        )

        loadQuestion()

        radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (!allowAnswer) return@setOnCheckedChangeListener

            allowAnswer = false
            countDown.cancel()

            val selected = optionButtons.indexOfFirst { it.id == checkedId }
            if (selected == -1) return@setOnCheckedChangeListener

            if (selected == questions[currentIndex].correctIndex) {
                correctAnswers++
                coins += 2

                // Update coins in the database immediately
                val currentCoins = db.getCoins()
                db.updateCoins(currentCoins + 2)
            } else {
                incorrectAnswers++
            }

            optionButtons.forEach { it.isEnabled = false }

            radioGroup.postDelayed({
                nextQuestion()
            }, 500)
        }
    }

    private fun loadQuestion() {
        if (currentIndex >= questions.size) {
            val intent = Intent(this, ResultActivity::class.java).apply {
                putExtra("correct", correctAnswers)
                putExtra("incorrect", incorrectAnswers)
                putExtra("coins", coins)
            }
            startActivity(intent)
            finish()
            return
        }

        val q = questions[currentIndex]
        questionNumberText.text = "Question ${currentIndex + 1}"
        questionText.text = q.text
        optionButtons.forEachIndexed { i, button ->
            button.text = q.options[i]
        }
        coinText.text = "Coins: $coins"

        allowAnswer = false
        radioGroup.clearCheck()
        optionButtons.forEach { it.isEnabled = true }

        // Wait a short time before re-enabling answer selection
        radioGroup.postDelayed({
            allowAnswer = true
        }, 100)

        countDown = object : CountDownTimer(12000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timerText.text = "Time left: ${millisUntilFinished / 1000}s"
            }

            override fun onFinish() {
                incorrectAnswers++
                nextQuestion()
            }
        }.start()
    }

    private fun nextQuestion() {
        currentIndex++
        loadQuestion()
    }
}
