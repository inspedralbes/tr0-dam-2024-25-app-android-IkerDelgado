package com.example.formulari0

import android.content.Context
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
import android.util.Log
import com.google.gson.Gson
import java.io.File
import java.io.FileWriter
import java.io.IOException

class QuestionsActivity : ComponentActivity() {
    private lateinit var preguntas: List<Pregunta>
    private var currentQuestionIndex: Int = 0
    private var correctAnswersCount: Int = 0 // Contador de respuestas correctas
    private lateinit var countDownTimer: CountDownTimer
    private var timeRemaining: Long = 30000 // 30 segundos

    // HashMap para almacenar las estadísticas de las preguntas
    private val preguntasStats: MutableMap<String, MutableMap<String, Int>> = mutableMapOf()

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
                    val textViewTimer = findViewById<TextView>(R.id.textViewTimer)

                    // Verifica si hay preguntas para mostrar
                    if (currentQuestionIndex < preguntas.size) {
                        val currentQuestion = preguntas[currentQuestionIndex]
                        textViewQuestion.text = currentQuestion.pregunta

                        // Carga la imagen desde la URL usando Picasso
                        Picasso.get()
                            .load(currentQuestion.imatge)
                            .into(imageViewQuestion)

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
                Toast.makeText(this@QuestionsActivity, "¡Temps esgotat!", Toast.LENGTH_SHORT).show()
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
                    ShowXmlLayout() // Mantiene la vista actualizada
                    // Muestra el tiempo restante
                    findViewById<TextView>(R.id.textViewTimer).text = "Temps restant: $secondsRemaining"
                }
            }
        }
    }

    private fun responder(selectedAnswer: Int) {
        val preguntaActual = preguntas[currentQuestionIndex]
        val esCorrecta = selectedAnswer == preguntaActual.resposta_correcta

        // Actualizamos las estadísticas para la pregunta actual usando solo su ID
        val stats = preguntasStats.getOrPut(preguntaActual.id.toString()) { mutableMapOf("correctas" to 0, "incorrectas" to 0) }
        if (esCorrecta) {
            correctAnswersCount++
            stats["correctas"] = stats["correctas"]!! + 1
        } else {
            stats["incorrectas"] = stats["incorrectas"]!! + 1
        }

        // Guardar las estadísticas en un archivo JSON después de cada respuesta
        guardarEstadisticasEnJson()

        // Enviar estadísticas al servidor
        enviarEstadisticasAlServidor()

        // Aumenta el índice de la pregunta actual
        currentQuestionIndex++

        // Muestra la siguiente pregunta o termina el cuestionario
        if (currentQuestionIndex < preguntas.size) {
            mostrarPreguntaActual() // Sigue mostrando la pregunta actual
        } else {
            mostrarResultados()
        }
    }


    // Función para guardar estadísticas en un archivo JSON local
    private fun guardarEstadisticasEnJson() {
        val gson = Gson()
        val jsonString = gson.toJson(preguntasStats)

        try {
            val file = File(applicationContext.filesDir, "preguntas_stats.json")
            val writer = FileWriter(file)
            writer.write(jsonString)
            writer.close()
            println("Estadísticas guardadas en JSON exitosamente.")

            // Imprime el JSON en la consola
            println("Contenido del JSON: $jsonString")
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    // Función para enviar estadísticas al servidor
    private fun enviarEstadisticasAlServidor() {
        val url = "http://192.168.0.160:3000/api/estadisticas" // Cambia esta URL según tu servidor

        // Crear un objeto que representa la estructura que espera el servidor
        val estadisticasEnviar = preguntasStats.map { (id, stats) ->
            mapOf(
                "id" to id,
                "correctas" to stats["correctas"],
                "incorrectas" to stats["incorrectas"]
            )
        }

        // Convertir a JSON
        val gson = Gson()
        val statsJson = gson.toJson(estadisticasEnviar)

        // Log para verificar el JSON que se está enviando
        Log.d("QuestionsActivity", "Enviando estadísticas: $statsJson")

        RetrofitClient.instance.enviarEstadisticas(url, statsJson).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Log.d("QuestionsActivity", "Estadísticas enviadas correctamente: ${response.message()}")
                } else {
                    Log.e("QuestionsActivity", "Error al enviar estadísticas: ${response.message()}")
                    response.errorBody()?.let {
                        Log.e("QuestionsActivity", "Cuerpo del error: ${it.string()}")
                    }
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("QuestionsActivity", "Error de red al enviar estadísticas: ${t.message}")
            }
        })
    }



    private fun mostrarResultados() {
        // Cancela el temporizador si está en ejecución
        countDownTimer.cancel()

        // Muestra la actividad de resultados cuando se terminen las preguntas
        val intent = Intent(this, ResultsActivity::class.java).apply {
            putExtra("correctes", correctAnswersCount)
            putExtra("total", preguntas.size)
        }
        startActivity(intent)
        finish() // Termina esta actividad
    }
}
