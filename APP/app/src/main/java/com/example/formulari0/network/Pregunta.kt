package com.example.formulari0.network

data class Pregunta(
    val id: Int,
    val pregunta: String,
    val respostes: List<String>,
    val resposta_correcta: Int,
    val imagenUrl: String // Asegúrate de que esta propiedad esté presente
)
