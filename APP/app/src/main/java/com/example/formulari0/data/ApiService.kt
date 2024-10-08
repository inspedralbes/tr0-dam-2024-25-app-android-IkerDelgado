package com.example.formulari0.data

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

interface ApiService {
    @GET("api/preguntes")
    suspend fun getPreguntes(): List<Pregunta>

    @GET("api/preguntes/{id}")
    suspend fun getPreguntaById(@Path("id") id: Int): Pregunta
}