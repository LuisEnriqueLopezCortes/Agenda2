package com.example.agenda2

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.util.Log
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import android.widget.EditText
import android.widget.Toast
import com.example.agenda2.network.RetrofitClient
import com.example.agenda2.network.ApiService
import com.example.agenda2.model.Respuesta

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody

class Registro : AppCompatActivity() {

        private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
        private var selectedImageUri: Uri? = null // Aquí se guarda la imagen seleccionada

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            enableEdgeToEdge()
            setContentView(R.layout.activity_registro)

            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = "Registro"

            ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
                val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
                v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
                insets
            }

            // 1. Registrar el launcher para seleccionar imagen
            imagePickerLauncher = registerForActivityResult(
                ActivityResultContracts.StartActivityForResult()
            ) { result ->
                if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                    selectedImageUri = result.data?.data
                    findViewById<ImageView>(R.id.avatarImageView).setImageURI(selectedImageUri)
                }
            }

            // 2. Botón para abrir galería
            findViewById<Button>(R.id.btnSeleccionarImagen).setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "image/*"
                imagePickerLauncher.launch(intent)
            }
            findViewById<Button>(R.id.RegistrarUsuario).setOnClickListener {
                enviarDatos()
            }
        }

        override fun onSupportNavigateUp(): Boolean {
            onBackPressedDispatcher.onBackPressed()
            return true
        }

        private fun enviarDatos() {
            val gmail = findViewById<EditText>(R.id.CorreoEt).text.toString()
            val nombre = findViewById<EditText>(R.id.NombreEt).text.toString()
            val apellido = findViewById<EditText>(R.id.ApellidoEt).text.toString()
            val alias = findViewById<EditText>(R.id.AliasEt).text.toString()
            val telefono = findViewById<EditText>(R.id.TelefonoEt).text.toString()
            val contrasena = findViewById<EditText>(R.id.ContraseñaEt).text.toString()

            // Validaciones básicas
            if (gmail.isBlank() || nombre.isBlank() || apellido.isBlank() || alias.isBlank() || telefono.isBlank() || contrasena.isBlank()) {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
                return
            }

            // Convertir los campos en RequestBody
            val gmailPart = RequestBody.create("text/plain".toMediaTypeOrNull(), gmail)
            val nombreUsuarioPart = RequestBody.create("text/plain".toMediaTypeOrNull(), nombre)
            val apellidoPart = RequestBody.create("text/plain".toMediaTypeOrNull(), apellido)
            val aliasPart = RequestBody.create("text/plain".toMediaTypeOrNull(), alias)
            val telefonoPart = RequestBody.create("text/plain".toMediaTypeOrNull(), telefono)
            val contrasenaPart = RequestBody.create("text/plain".toMediaTypeOrNull(), contrasena)

            // Imagen (si hay)
            val imagenPart = if (selectedImageUri != null) {
                val inputStream = contentResolver.openInputStream(selectedImageUri!!)
                val imagenBytes = inputStream!!.readBytes()
                val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), imagenBytes)
                MultipartBody.Part.createFormData("imagen", "avatar.jpg", requestFile)
            } else {
                null
            }

            // Llamar a la API
            val call = RetrofitClient.apiService.registerUser(
                gmailPart,  nombreUsuarioPart, apellidoPart, aliasPart, contrasenaPart, telefonoPart, imagenPart
            )

            call.enqueue(object : Callback<Respuesta> {
                override fun onResponse(call: Call<Respuesta>, response: Response<Respuesta>) {
                    if (response.isSuccessful && response.body() != null) {
                        val respuesta = response.body()
                        Log.d("RegistroResponse", "Mensaje: ${respuesta?.message}")
                        Toast.makeText(this@Registro, respuesta?.message ?: "Registro exitoso", Toast.LENGTH_LONG).show()
                    } else {
                        val errorBody = response.errorBody()?.string()
                        Log.e("RegistroError", "Código: ${response.code()}, Cuerpo: $errorBody")
                        Toast.makeText(this@Registro, "Error del servidor: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }

                override fun onFailure(call: Call<Respuesta>, t: Throwable) {
                    Toast.makeText(this@Registro, "Error en la conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }