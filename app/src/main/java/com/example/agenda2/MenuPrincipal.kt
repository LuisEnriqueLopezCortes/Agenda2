package com.example.agenda2

import android.content.Context
import android.widget.*
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import com.example.agenda2.AgregarNota.Agregar_Nota
import com.example.agenda2.ListarNotas.Listar_Notas
import com.example.agenda2.Perfil.Perfil_Usuario

class MenuPrincipal : AppCompatActivity() {

    private lateinit var uidTextView: TextView
    private lateinit var correoTextView: TextView
    private lateinit var aliasTextView: TextView
    private lateinit var progressBar: ProgressBar

    private lateinit var btnAgregarNotas: Button
    private lateinit var btnListarNotas: Button
    private lateinit var btnPerfil: Button
    private lateinit var btnCerrarSesion: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_principal)

        // Ocultar título
        supportActionBar?.hide()

        // Referencias a vistas
        uidTextView = findViewById(R.id.UidPrincipal)
        correoTextView = findViewById(R.id.CorreoPrincipal)
        aliasTextView = findViewById(R.id.AliasPrincipal) // aquí alias = NombresPrincipal
        progressBar = findViewById(R.id.progressBarDatos)

        btnAgregarNotas = findViewById(R.id.AgregarNotas)
        btnListarNotas = findViewById(R.id.ListarNotas)
        btnPerfil = findViewById(R.id.Perfil)
        btnCerrarSesion = findViewById(R.id.CerrarSesion)

        // Cargar datos del usuario
        cargarDatosUsuario()

        // Acciones de botones
        btnAgregarNotas.setOnClickListener {
            startActivity(Intent(this, Agregar_Nota::class.java))
        }

        btnListarNotas.setOnClickListener {
            startActivity(Intent(this, Listar_Notas::class.java))
        }

        btnPerfil.setOnClickListener {
            startActivity(Intent(this, Perfil_Usuario::class.java))
        }

        btnCerrarSesion.setOnClickListener {
            cerrarSesion()
        }
    }

    private fun cargarDatosUsuario() {
        val sharedPref = getSharedPreferences("usuario", Context.MODE_PRIVATE)
        val id = sharedPref.getInt("id", -1)
        val gmail = sharedPref.getString("gmail", null)
        val alias = sharedPref.getString("alias", null)

        if (id != -1 && gmail != null && alias != null) {
            uidTextView.text = "ID: $id"
            correoTextView.text = "Correo: $gmail"
            aliasTextView.text = "Alias: $alias"

            uidTextView.visibility = View.VISIBLE
            correoTextView.visibility = View.VISIBLE
            aliasTextView.visibility = View.VISIBLE

            // Activar botones y ocultar el progreso
            habilitarBotones()
        } else {
            Toast.makeText(this, "Error al cargar los datos del usuario", Toast.LENGTH_SHORT).show()
            progressBar.visibility = View.GONE
        }
    }

    private fun habilitarBotones() {
        progressBar.visibility = View.GONE
        btnAgregarNotas.isEnabled = true
        btnListarNotas.isEnabled = true
        btnPerfil.isEnabled = true
        btnCerrarSesion.isEnabled = true
    }

    private fun cerrarSesion() {
        val sharedPref = getSharedPreferences("usuario", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, Login::class.java))
        finish()
    }
}