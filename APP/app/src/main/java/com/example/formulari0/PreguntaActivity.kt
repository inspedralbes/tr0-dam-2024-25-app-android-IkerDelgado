package com.example.formulari0

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.formulari0.data.ApiService
import com.example.formulari0.data.Pregunta
import com.example.formulari0.data.PreguntesResponse
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class PreguntaActivity : AppCompatActivity() {

    private lateinit var listaPreguntas: List<Pregunta>
    private var preguntaActual = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pregunta)

        // Aquí iría el código para obtener las preguntas desde tu servidor
        obtenerPreguntas()

        val botonSiguiente: Button = findViewById(R.id.botonSiguiente)
        botonSiguiente.setOnClickListener {
            // Lógica para mostrar la siguiente pregunta
            preguntaActual++
            if (preguntaActual < listaPreguntas.size) {
                cargarPregunta()
            } else {
                // Ir a la pantalla de resultados
                val intent = Intent(this, ResultadosActivity::class.java)
                startActivity(intent)
            }
        }
    }

    // Método para cargar la pregunta
    private fun cargarPregunta() {
        val preguntaActual = listaPreguntas[preguntaActual]
        findViewById<ImageView>(R.id.imagenPregunta).setImageResource(preguntaActual.imagenResId)
        findViewById<TextView>(R.id.textoPregunta).text = preguntaActual.texto
        findViewById<Button>(R.id.respuesta1).text = preguntaActual.respuestas[0]
        findViewById<Button>(R.id.respuesta2).text = preguntaActual.respuestas[1]
        findViewById<Button>(R.id.respuesta3).text = preguntaActual.respuestas[2]
        findViewById<Button>(R.id.respuesta4).text = preguntaActual.respuestas[3]
    }

    private fun obtenerPreguntas() {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("http://tu-servidor.com/api/preguntas") // Cambia esta URL a la de tu servidor
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
            }

            override fun onResponse(call: Call, response: Response) {
                if (response.isSuccessful) {
                    val jsonResponse = response.body?.string()
                    // Aquí puedes parsear el JSON y cargar las preguntas
                    // Por ejemplo, usando Gson para deserializar
                }
            }
        })
    }
}