package com.example.agenda2.network

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import com.example.agenda2.network.ApiService
import okhttp3.logging.HttpLoggingInterceptor

object RetrofitClient {
    private const val BASE_URL = "https://agendaback-7oq6.onrender.com" // cámbialo

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()


    val apiService: ApiService by lazy {
        val client = OkHttpClient.Builder().build() // <-- opcional, si necesitas configuraciones especiales

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // <-- opcional, puede quitarse si no necesitas interceptores
            .addConverterFactory(ScalarsConverterFactory.create()) // <-- si alguna respuesta es texto plano
            .addConverterFactory(GsonConverterFactory.create()) // <-- para respuestas JSON normales
            .build()

        retrofit.create(ApiService::class.java)
    }
}