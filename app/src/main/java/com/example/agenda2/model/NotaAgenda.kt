package com.example.agenda2.model

import java.io.Serializable

data class NotaAgenda(
    val id: Int,
    val id_usuario: Int,
    val titulo: String,
    val descripcion: String,
    val fecha_evento: String,
    val estado: String,
    val imagen: String?   // Imágenes o documentos opcionales
): Serializable