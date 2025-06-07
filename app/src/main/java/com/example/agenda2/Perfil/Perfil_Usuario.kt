package com.example.agenda2.Perfil

import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.agenda2.R
import com.example.agenda2.model.Respuesta
import com.example.agenda2.model.Usuario
import com.example.agenda2.model.RespuestaActualizacion
import okhttp3.RequestBody.Companion.asRequestBody
import com.example.agenda2.utils.FileUtil
import com.example.agenda2.network.ApiService
import com.example.agenda2.network.RetrofitClient
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import android.util.Log
import retrofit2.Callback
import retrofit2.Response
import com.example.agenda2.MenuPrincipal
import java.io.File

class Perfil_Usuario : AppCompatActivity() {
    private lateinit var imagenPerfil: ImageView
    private lateinit var editarImagen: ImageView

    private lateinit var correo: TextView
    private lateinit var uid: TextView
    private lateinit var nombre: EditText
    private lateinit var apellido: EditText
    private lateinit var alias: EditText
    private lateinit var telefono: EditText

    private lateinit var btnGuardar: Button
    private lateinit var btnCambiarPassword: Button

    private var imagenUri: Uri? = null
    private var imagenCambiada = false

    private val apiService = RetrofitClient.apiService


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil_usuario)


        imagenPerfil = findViewById(R.id.Imagen_Perfil)
        editarImagen = findViewById(R.id.Editar_imagen)

        correo = findViewById(R.id.Perfil_Gmail)
        uid = findViewById(R.id.Perfil_Id)
        nombre = findViewById(R.id.Perfil_Nombre)
        apellido = findViewById(R.id.Perfil_Apellido)
        alias = findViewById(R.id.Perfil_Alias)
        telefono = findViewById(R.id.Perfil_Telefono)

        btnGuardar = findViewById(R.id.Perfil_GuardarCambios)
        btnCambiarPassword=findViewById(R.id.Perfil_CambiarContrasena)

        editarImagen.setOnClickListener { mostrarOpcionesImagen() }
        btnGuardar.setOnClickListener { guardarCambios() }
        btnCambiarPassword.setOnClickListener {
            val intent = Intent(this, EditarPassword::class.java)
            startActivity(intent)
        }


        cargarDatosUsuario()
    }

    private fun cargarDatosUsuario() {
        val sharedPref = getSharedPreferences("usuario", MODE_PRIVATE)
        val idUsuario = sharedPref.getInt("id", -1)

        if (idUsuario == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        apiService.getUsuarioPorId(idUsuario).enqueue(object : Callback<Respuesta> {
            override fun onResponse(call: Call<Respuesta>, response: Response<Respuesta>) {
                if (response.isSuccessful && response.body()?.usuario != null) {
                    val user = response.body()!!.usuario!!
                    correo.text = user.gmail
                    uid.text = user.id.toString()
                    nombre.setText(user.nombre_usuario)
                    apellido.setText(user.apellido)
                    alias.setText(user.alias)
                    telefono.setText(user.telefono)


                    Glide.with(this@Perfil_Usuario)
                        .load("https://agendaback-7oq6.onrender.com/" + user.imagen)
                        .placeholder(R.drawable.imagen_perfil_usuario)
                        .into(imagenPerfil)
                    Log.d("PERFIL", "Ruta de imagen recibida: ${user.imagen}")
                } else {
                    Toast.makeText(this@Perfil_Usuario, "No se encontró el usuario", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Respuesta>, t: Throwable) {
                Toast.makeText(this@Perfil_Usuario, "Error al cargar datos: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarOpcionesImagen() {
        val opciones = arrayOf("Galería", "Cámara")
        AlertDialog.Builder(this)
            .setTitle("Seleccionar imagen")
            .setItems(opciones) { _, cual ->
                when (cual) {
                    0 -> abrirGaleria()
                    1 -> abrirCamara()
                }
            }
            .show()
    }

    private fun abrirGaleria() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        galeriaLauncher.launch(intent)
    }

    private val galeriaLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagenUri = result.data?.data
                imagenPerfil.setImageURI(imagenUri)
                imagenCambiada = true
            }
        }

    private fun abrirCamara() {
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "Foto nueva")
        imagenUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imagenUri)
        camaraLauncher.launch(intent)
    }

    private val camaraLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                imagenUri?.let {
                    imagenPerfil.setImageURI(it)
                    imagenCambiada = true
                }
            }
        }

    private fun guardarCambios() {
        val nombreTxt = nombre.text.toString()
        val apellidoTxt = apellido.text.toString()
        val aliasTxt = alias.text.toString()
        val telefonoTxt = telefono.text.toString()
        val sharedPref = getSharedPreferences("usuario", MODE_PRIVATE)
        val idUsuario = sharedPref.getInt("id", -1)

        if (idUsuario == -1) {
            Toast.makeText(this, "ID de usuario no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        val map = HashMap<String, RequestBody>().apply {
            put("nombre_usuario", nombreTxt.toRequestBody())
            put("apellido", apellidoTxt.toRequestBody())
            put("alias", aliasTxt.toRequestBody())
            put("telefono", telefonoTxt.toRequestBody())
        }

        val imagenPart: MultipartBody.Part? = if (imagenCambiada && imagenUri != null) {
            val file = FileUtil.from(this, imagenUri!!)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            MultipartBody.Part.createFormData("imagen", file.name, requestFile)
        } else null

        apiService.actualizarUsuario(idUsuario, map, imagenPart)
            .enqueue(object : Callback<RespuestaActualizacion> {
                override fun onResponse(call: Call<RespuestaActualizacion>, response: Response<RespuestaActualizacion>) {
                    if (response.isSuccessful) {
                        val editor = getSharedPreferences("usuario", MODE_PRIVATE).edit()
                        editor.putString("nombre_usuario", nombreTxt)
                        editor.putString("apellido", apellidoTxt)
                        editor.putString("alias", aliasTxt)
                        editor.putString("telefono", telefonoTxt)
                        editor.apply()
                        Toast.makeText(this@Perfil_Usuario, "Cambios guardados", Toast.LENGTH_SHORT).show()

                        val intent = Intent(this@Perfil_Usuario, MenuPrincipal::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@Perfil_Usuario, "Error al guardar", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<RespuestaActualizacion>, t: Throwable) {
                    Toast.makeText(this@Perfil_Usuario, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }

    private fun String.toRequestBody(): RequestBody =
        RequestBody.create("text/plain".toMediaTypeOrNull(), this)
}