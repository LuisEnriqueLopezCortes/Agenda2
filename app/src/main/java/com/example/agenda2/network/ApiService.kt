package com.example.agenda2.network

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import com.example.agenda2.model.Respuesta
import com.example.agenda2.model.RespuestaActualizacion
import com.example.agenda2.model.ActualizarPasswordRequest
import retrofit2.Call
import retrofit2.http.Multipart
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.PartMap
import retrofit2.http.GET
import retrofit2.http.Body
import retrofit2.http.Part

interface ApiService {
    @Multipart
    @POST("api/register")
    fun registerUser(
        @Part("gmail") gmail: RequestBody,
        @Part("nombre_usuario") nombre: RequestBody,
        @Part("apellido") apellido: RequestBody,
        @Part("alias") alias: RequestBody,
        @Part("contrasena") contrasena: RequestBody,
        @Part("telefono") telefono: RequestBody,
        @Part imagen: MultipartBody.Part? // puede ser null
    ): Call<Respuesta>

    @FormUrlEncoded
    @POST("/api/login")
    fun loginUser(
        @Field("gmail") gmail: String,
        @Field("contrasena") contrasena: String
    ): Call<Respuesta>

    @Multipart
    @PUT("/api/usuarios/{id}")
    fun actualizarUsuario(
        @Path("id") id: Int,
        @PartMap datos: Map<String, @JvmSuppressWildcards RequestBody>,
        @Part imagen: MultipartBody.Part?
    ): Call<RespuestaActualizacion>

    @GET("/api/usuarios/id/{id}")
    fun getUsuarioPorId(
        @Path("id") id: Int
    ): Call<Respuesta>

    @PUT("/api/usuarios/actualizarPassword") // Asegúrate que coincide con tu ruta del backend
    fun actualizarPassword(
        @Body request: ActualizarPasswordRequest
    ): Call<Respuesta>
}