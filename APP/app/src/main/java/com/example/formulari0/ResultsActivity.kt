package com.example.formulari0

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.formulari0.ui.theme.Formulari0Theme

class ResultsActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Obtener los resultados de la intent
        val correctas = intent.getIntExtra("correctes", 0)
        val total = intent.getIntExtra("total", 0)

        setContent {
            Formulari0Theme {
                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF03DAC5)), // Color de fondo turquesa
                    color = Color.Transparent // Hacemos que la Surface sea transparente para que el fondo se vea
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "¡Questionari acabat!",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Respostes correctes: $correctas de $total",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.height(24.dp))

                        Button(
                            onClick = {
                                val intent = Intent(this@ResultsActivity, MainActivity::class.java)
                                startActivity(intent)
                                finish()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF27B4C8),
                                contentColor = Color.White
                            )
                        ) {
                            Text(text = "Tornar al Inici")
                        }
                    }
                }
            }
        }
    }
}
