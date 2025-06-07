package com.example.agenda2.Perfil

import android.app.ProgressDialog
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agenda2.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.agenda2.network.RetrofitClient
import com.example.agenda2.model.ActualizarPasswordRequest
import com.example.agenda2.Login
import com.example.agenda2.model.Respuesta

class EditarPassword : AppCompatActivity() {

    private lateinit var actualPassText: TextView
    private lateinit var etActualPass: EditText
    private lateinit var etNuevoPass: EditText
    private lateinit var etRNuevoPass: EditText
    private lateinit var btnActualizarPass: Button
    private lateinit var progressDialog: ProgressDialog

    private lateinit var sharedPreferences: SharedPreferences
    private val apiService = RetrofitClient.apiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editar_password)

        supportActionBar?.apply {
            title = "Cambiar contraseña"
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        inicializarVariables()

        btnActualizarPass.setOnClickListener {
            val passActual = etActualPass.text.toString().trim()
            val nuevoPass = etNuevoPass.text.toString().trim()
            val rNuevoPass = etRNuevoPass.text.toString().trim()

            when {
                passActual.isEmpty() -> etActualPass.error = "Llene el campo"
                nuevoPass.isEmpty() -> etNuevoPass.error = "Llene el campo"
                rNuevoPass.isEmpty() -> etRNuevoPass.error = "Llene el campo"
                nuevoPass != rNuevoPass -> showToast("Las contraseñas no coinciden")
                nuevoPass.length < 6 -> etNuevoPass.error = "Debe ser igual o mayor de 6 caracteres"
                else -> actualizarPassword(passActual, nuevoPass)
            }
        }
    }

    private fun inicializarVariables() {
        actualPassText = findViewById(R.id.ActualPass)
        etActualPass = findViewById(R.id.EtActualPass)
        etNuevoPass = findViewById(R.id.EtNuevoPass)
        etRNuevoPass = findViewById(R.id.EtRNuevoPass)
        btnActualizarPass = findViewById(R.id.BtnActualizarPass)

        progressDialog = ProgressDialog(this)
        sharedPreferences = getSharedPreferences("usuario", MODE_PRIVATE)

        actualPassText.text = "Por seguridad no se muestra la contraseña actual"
    }

    private fun actualizarPassword(passActual: String, nuevoPass: String) {
        val gmail = sharedPreferences.getString("gmail", null)

        if (gmail.isNullOrEmpty()) {
            showToast("No se encontró el correo del usuario")
            return
        }

        progressDialog.setTitle("Actualizando")
        progressDialog.setMessage("Espere por favor")
        progressDialog.show()

        val request = ActualizarPasswordRequest(gmail, passActual, nuevoPass)

        apiService.actualizarPassword(request).enqueue(object : Callback<Respuesta> {
            override fun onResponse(call: Call<Respuesta>, response: Response<Respuesta>) {
                progressDialog.dismiss()
                if (response.isSuccessful && response.body()?.success == true) {
                    showToast("Contraseña cambiada con éxito")
                    cerrarSesion()
                } else {
                    showToast(response.body()?.message ?: "Error al actualizar la contraseña")
                }
            }

            override fun onFailure(call: Call<Respuesta>, t: Throwable) {
                progressDialog.dismiss()
                showToast("Error: ${t.message}")
            }
        })
    }

    private fun cerrarSesion() {
        sharedPreferences.edit().clear().apply()
        val intent = Intent(this, Login::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun showToast(msg: String) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }
}
