package com.example.agenda2.AgregarNota


import com.example.agenda2.AgregarNota.Agregar_Nota

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.agenda2.R
import com.example.agenda2.network.RetrofitClient
import com.example.agenda2.model.Respuesta
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.util.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.agenda2.model.NotaAgenda
import android.util.Log

class Agregar_Nota : AppCompatActivity() {
    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>
    private var selectedImageUri: Uri? = null
    private lateinit var fechaSeleccionada: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_agregar_nota)

        val btnCalendario = findViewById<Button>(R.id.Btn_Calendario)
        val fechaText = findViewById<TextView>(R.id.Fecha)
        val btnSeleccionarImagen = findViewById<Button>(R.id.Btn_SeleccionarImagenAgenda)
        val imagenPreview = findViewById<ImageView>(R.id.ImagenPreviewAgenda)
        val btnGuardarNota = findViewById<Button>(R.id.Btn_GuardarNota)

        // 1. DatePicker
        btnCalendario.setOnClickListener {
            val calendario = Calendar.getInstance()
            val year = calendario.get(Calendar.YEAR)
            val month = calendario.get(Calendar.MONTH)
            val day = calendario.get(Calendar.DAY_OF_MONTH)

            val datePicker = DatePickerDialog(this, { _, y, m, d ->
                fechaSeleccionada = String.format("%04d-%02d-%02d", y, m + 1, d)
                fechaText.text = fechaSeleccionada
            }, year, month, day)
            datePicker.show()
        }

        // 2. Image picker
        imagePickerLauncher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                selectedImageUri = result.data?.data
                imagenPreview.setImageURI(selectedImageUri)
                imagenPreview.visibility = ImageView.VISIBLE
            }
        }

        btnSeleccionarImagen.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            imagePickerLauncher.launch(intent)
        }

        // 3. Guardar nota
        btnGuardarNota.setOnClickListener {
            enviarNota()
        }
    }

    private fun enviarNota() {
        val titulo = findViewById<EditText>(R.id.Titulo).text.toString()
        val descripcion = findViewById<EditText>(R.id.Descripcion).text.toString()
        val estado = findViewById<Spinner>(R.id.EstadoSpinner).selectedItem.toString()

        if (titulo.isBlank() || descripcion.isBlank() || !::fechaSeleccionada.isInitialized) {
            Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val idUsuario = getSharedPreferences("usuario", MODE_PRIVATE).getInt("id", -1)
        if (idUsuario == -1) {
            Toast.makeText(this, "Usuario no válido", Toast.LENGTH_SHORT).show()
            return
        }

        val idUsuarioPart = RequestBody.create("text/plain".toMediaTypeOrNull(), idUsuario.toString())
        val tituloPart = RequestBody.create("text/plain".toMediaTypeOrNull(), titulo)
        val descripcionPart = RequestBody.create("text/plain".toMediaTypeOrNull(), descripcion)
        val fechaPart = RequestBody.create("text/plain".toMediaTypeOrNull(), fechaSeleccionada)
        val estadoPart = RequestBody.create("text/plain".toMediaTypeOrNull(), estado)

        val imagenPart = selectedImageUri?.let {
            val inputStream = contentResolver.openInputStream(it)
            val bytes = inputStream!!.readBytes()
            val requestFile = RequestBody.create("image/*".toMediaTypeOrNull(), bytes)
            MultipartBody.Part.createFormData("imagen", "nota.jpg", requestFile)
        }

        val map = mutableMapOf<String, RequestBody>()
        map["id_usuario"] = idUsuarioPart
        map["titulo"] = tituloPart
        map["descripcion"] = descripcionPart
        map["fecha_evento"] = fechaPart
        map["estado"] = estadoPart

        val call = RetrofitClient.apiService.agregarNota(map, imagenPart)

        call.enqueue(object : Callback<NotaAgenda> {
            override fun onResponse(call: Call<NotaAgenda>, response: Response<NotaAgenda>) {
                val nota = response.body()
                if (nota != null) {
                    val imageUrl = "https://tudominio.com/" + nota.imagen  // CAMBIA esto por tu dominio real (ej: render.com o InfinityFree)
                    Log.d("NotaImagen", "URL completa de imagen: $imageUrl")
                    // Puedes cargar la imagen en un ImageView si quieres con Glide o Picasso
                }

                if (response.isSuccessful) {
                    val nota = response.body()
                    Toast.makeText(this@Agregar_Nota, "Nota agregada: ${nota?.titulo}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(this@Agregar_Nota, "Error en el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<NotaAgenda>, t: Throwable) {
                Toast.makeText(this@Agregar_Nota, "Fallo en la conexión: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}