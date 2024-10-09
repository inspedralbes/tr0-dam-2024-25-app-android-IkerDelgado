package com.example.formulari0.network

import retrofit2.Call
import retrofit2.http.GET

interface PreguntaService {
    @GET("/api/preguntes")
    fun getPreguntas(): Call<List<Pregunta>>
}