package com.example.todo

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class ResultActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_result)

        val correct = intent.getIntExtra("correct", 0)
        val incorrect = intent.getIntExtra("incorrect", 0)
        val coins = intent.getIntExtra("coins", 0)

        findViewById<TextView>(R.id.resultText).text =
            "Correct: $correct\nIncorrect: $incorrect\nCoins won: $coins"
    }

    fun onBackPressed(view: View) {
        onBackPressedDispatcher.onBackPressed()
    }

}