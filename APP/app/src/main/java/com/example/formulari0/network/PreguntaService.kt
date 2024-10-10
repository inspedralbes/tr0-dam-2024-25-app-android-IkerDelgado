package com.example.formulari0.network

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Url

interface PreguntaService {
    @GET("/api/preguntes")
    fun getPreguntas(): Call<List<Pregunta>>
    @POST
    fun enviarEstadisticas(@Url url: String, @Body stats: String): Call<Void>
}