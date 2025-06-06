package com.example.agenda2.model

data class Usuario(
    val id: Int,
    val gmail: String,
    val nombre_usuario: String,
    val apellido: String,
    val alias: String,
    val telefono: String,
    val imagen: String? // Puede ser null si no tiene imagen
)