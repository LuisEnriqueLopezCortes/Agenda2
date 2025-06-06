package com.example.agenda2.model


data class Respuesta(
    val success: Boolean,
    val message: String,
    val usuario: Usuario? // Este es el objeto que te devolverá el backend
)

