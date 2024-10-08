package com.example.formulari0.network

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://a23ikedelgra.dam.inspedralbes.cat:29876/"

    val instance: PreguntaService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(PreguntaService::class.java)
    }
}