package com.example.formulari0.data


data class Pregunta(
    val id: Int,
    val pregunta: String,
    val respostes: List<String>,
    val resposta_correcta: Int,
    val imatge: String
)