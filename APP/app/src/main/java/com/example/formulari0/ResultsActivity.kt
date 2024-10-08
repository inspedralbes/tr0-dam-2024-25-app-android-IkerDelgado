package com.example.formulari0

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.formulari0.R

class ResultsActivity : AppCompatActivity() {

    private lateinit var resultText: TextView
    private lateinit var restartButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_results)

        resultText = findViewById(R.id.resultText)
        restartButton = findViewById(R.id.restartButton)

        val correctCount = intent.getIntExtra("correctCount", 0)
        val totalCount = intent.getIntExtra("totalCount", 1)

        resultText.text = "Respuestas Correctas: $correctCount / $totalCount"

        restartButton.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish() // Cerrar la actividad actual
        }
    }
}
