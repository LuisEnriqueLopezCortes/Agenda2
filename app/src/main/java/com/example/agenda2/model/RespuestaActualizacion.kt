package com.example.agenda2.model

data class RespuestaActualizacion(
    val mensaje: String, // o lo que tu API retorne
    val usuarioActualizado: Usuario? // si retorna el usuario actualizado
)