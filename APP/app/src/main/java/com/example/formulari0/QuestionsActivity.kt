package com.example.formulari0

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.formulari0.network.Pregunta
import com.example.formulari0.network.RetrofitClient
import com.example.formulari0.ui.theme.Formulari0Theme
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.CountDownTimer

class QuestionsActivity : ComponentActivity() {
    private lateinit var preguntas: List<Pregunta>
    private var currentQuestionIndex: Int = 0
    private var correctAnswersCount: Int = 0 // Contador de respuestas correctas
    private lateinit var countDownTimer: CountDownTimer
    private var timeRemaining: Long = 30000 // 30 segundos

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Llama a obtenerPreguntas
        obtenerPreguntas()
    }

    private fun obtenerPreguntas() {
        RetrofitClient.instance.getPreguntas().enqueue(object : Callback<List<Pregunta>> {
            override fun onResponse(call: Call<List<Pregunta>>, response: Response<List<Pregunta>>) {
                if (response.isSuccessful) {
                    preguntas = response.body() ?: emptyList()
                    mostrarPreguntaActual()
                    iniciarTemporizador() // Iniciar el temporizador al cargar preguntas
                } else {
                    // Maneja el error de respuesta aquí
                    mostrarError("Error al cargar preguntas: ${response.message()}")
                }
            }

            override fun onFailure(call: Call<List<Pregunta>>, t: Throwable) {
                // Maneja la falla de la solicitud aquí
                mostrarError("Error de red: ${t.message}")
            }
        })
    }

    private fun mostrarError(mensaje: String) {
        // Maneja y muestra errores en la UI
        setContent {
            Formulari0Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    androidx.compose.material3.Text(text = mensaje) // Muestra el mensaje de error
                }
            }
        }
    }

    private fun mostrarPreguntaActual() {
        setContent {
            Formulari0Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowXmlLayout()
                }
            }
        }
    }

    @Composable
    fun ShowXmlLayout(modifier: Modifier = Modifier) {
        AndroidView(
            modifier = modifier,
            factory = { context ->
                LayoutInflater.from(context).inflate(R.layout.activity_questions, null).apply {
                    // Configura los elementos de la vista
                    val textViewQuestion = findViewById<TextView>(R.id.textViewQuestion)
                    val imageViewQuestion = findViewById<ImageView>(R.id.imageViewQuestion)
                    val buttonAnswer1 = findViewById<Button>(R.id.buttonAnswer1)
                    val buttonAnswer2 = findViewById<Button>(R.id.buttonAnswer2)
                    val buttonAnswer3 = findViewById<Button>(R.id.buttonAnswer3)
                    val buttonAnswer4 = findViewById<Button>(R.id.buttonAnswer4)
                    val textViewTimer = findViewById<TextView>(R.id.textViewTimer) // Añade un TextView para mostrar el temporizador

                    // Verifica si hay preguntas para mostrar
                    if (currentQuestionIndex < preguntas.size) {
                        val currentQuestion = preguntas[currentQuestionIndex]
                        textViewQuestion.text = currentQuestion.pregunta

                        // Carga la imagen desde la URL
                        Picasso.get().load(currentQuestion.imagenUrl).into(imageViewQuestion)

                        // Asegúrate de que las respuestas tengan al menos 4 opciones
                        if (currentQuestion.respostes.size >= 4) {
                            buttonAnswer1.text = currentQuestion.respostes[0]
                            buttonAnswer2.text = currentQuestion.respostes[1]
                            buttonAnswer3.text = currentQuestion.respostes[2]
                            buttonAnswer4.text = currentQuestion.respostes[3]
                        }

                        // Configura los listeners para las respuestas
                        buttonAnswer1.setOnClickListener { responder(0) }
                        buttonAnswer2.setOnClickListener { responder(1) }
                        buttonAnswer3.setOnClickListener { responder(2) }
                        buttonAnswer4.setOnClickListener { responder(3) }
                    }
                }
            }
        )
    }

    private fun iniciarTemporizador() {
        // Temporizador de 30 segundos
        countDownTimer = object : CountDownTimer(timeRemaining, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeRemaining = millisUntilFinished
                // Aquí actualiza el UI para mostrar el tiempo restante
                val secondsRemaining = (millisUntilFinished / 1000).toInt()
                mostrarTiempoRestante(secondsRemaining)
            }

            override fun onFinish() {
                // Cuando el tiempo se agote, redirige a la pantalla de resultados
                Toast.makeText(this@QuestionsActivity, "¡Tiempo agotado!", Toast.LENGTH_SHORT).show()
                mostrarResultados()
            }
        }.start()
    }

    private fun mostrarTiempoRestante(secondsRemaining: Int) {
        // Actualiza la UI para mostrar el tiempo restante
        setContent {
            Formulari0Theme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ShowXmlLayout()
                    // Aquí puedes añadir un TextView para mostrar el tiempo restante
                    findViewById<TextView>(R.id.textViewTimer).text = "Tiempo restante: $secondsRemaining"
                }
            }
        }
    }

    private fun responder(selectedAnswer: Int) {
        // Verifica si la respuesta seleccionada es correcta
        if (selectedAnswer == preguntas[currentQuestionIndex].resposta_correcta) {
            correctAnswersCount++ // Incrementa el contador de respuestas correctas
        }

        // Aumenta el índice de la pregunta actual
        currentQuestionIndex++

        // Muestra la siguiente pregunta o termina el cuestionario
        if (currentQuestionIndex < preguntas.size) {
            mostrarPreguntaActual() // Muestra la siguiente pregunta
        } else {
            // Muestra la actividad de resultados cuando se terminen las preguntas
            mostrarResultados()
        }
    }

    private fun mostrarResultados() {
        // Cancela el temporizador si está en ejecución
        countDownTimer.cancel()

        // Muestra la actividad de resultados cuando se terminen las preguntas
        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra("correctAnswersCount", correctAnswersCount) // Pasar el conteo de respuestas correctas
            putExtra("totalQuestionsCount", preguntas.size) // Pasar el total de preguntas
        }
        startActivity(intent)
        finish() // Termina esta actividad
    }
}
