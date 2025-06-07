package com.example.agenda2.model

data class ActualizarPasswordRequest(
    val gmail: String,
    val passwordActual: String,
    val nuevoPassword: String
)