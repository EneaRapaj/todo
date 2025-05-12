package com.example.todo

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.util.*

class GameActivity : AppCompatActivity() {

    private lateinit var rabbit: ImageView
    private lateinit var hitsText: TextView
    private lateinit var coinsText: TextView
    private lateinit var heart1: ImageView
    private lateinit var heart2: ImageView
    private lateinit var heart3: ImageView
    private var hits = 8
    private var coins = 0
    private var hearts = 3
    private lateinit var dbHelper: MyDBHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game)

        rabbit = findViewById(R.id.rabbit)
        hitsText = findViewById(R.id.hitsText)
        coinsText = findViewById(R.id.coinsText)
        heart1 = findViewById(R.id.heart1)
        heart2 = findViewById(R.id.heart2)
        heart3 = findViewById(R.id.heart3)
        dbHelper = MyDBHelper(this)

        // Load current coins from database
        coins = dbHelper.getCoins()

        updateUI()
        startGame()

        rabbit.setOnClickListener {
            hits--
            coins++
            updateUI()
            checkGameStatus()
        }
    }

    private fun startGame() {
        object : CountDownTimer(30000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                moveRabbit()
            }
            override fun onFinish() {
                if (hearts > 0) showSuccessScreen()
            }
        }.start()
    }

    private fun moveRabbit() {
        val random = Random()
        rabbit.x = random.nextInt(800).toFloat()
        rabbit.y = random.nextInt(1000).toFloat()
        rabbit.visibility = View.VISIBLE

        rabbit.postDelayed({
            rabbit.visibility = View.INVISIBLE
            if (rabbit.visibility == View.INVISIBLE) {
                hearts--
                updateUI()
                checkGameStatus()
            }
        }, 700)
    }

    private fun checkGameStatus() {
        if (hearts == 0) {
            showGameOverScreen()
        } else if (hits == 0) {
            saveCoinsToDatabase()
            showSuccessScreen()
        }
    }

    private fun showGameOverScreen() {
        setContentView(R.layout.activity_game_over)
        findViewById<Button>(R.id.retryButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun showSuccessScreen() {
        setContentView(R.layout.activity_success)
        findViewById<Button>(R.id.mainButton).setOnClickListener {
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }

    private fun updateUI() {
        hitsText.text = "Hits: $hits"
        coinsText.text = "Coins: $coins"
        heart1.setImageResource(if (hearts >= 1) R.drawable.red_heart else R.drawable.black_heart)
        heart2.setImageResource(if (hearts >= 2) R.drawable.red_heart else R.drawable.black_heart)
        heart3.setImageResource(if (hearts >= 3) R.drawable.red_heart else R.drawable.black_heart)
    }

    private fun saveCoinsToDatabase() {
        // Retrieve current coins from the database
        val currentCoins = dbHelper.getCoins()

        // Add the coins earned in this game
        val newTotalCoins = currentCoins + coins

        // Update the database
        dbHelper.updateCoins(newTotalCoins)
    }
}
