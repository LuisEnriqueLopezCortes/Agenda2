package com.example.agenda2


import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.agenda2.model.Respuesta
import com.example.agenda2.network.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Login : AppCompatActivity() {

    private lateinit var correoLogin: EditText
    private lateinit var passLogin: EditText
    private lateinit var btnLogeo: Button
    private lateinit var usuarioNuevoTxt: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Login"

        correoLogin = findViewById(R.id.CorreoLogin)
        passLogin = findViewById(R.id.PassLogin)
        btnLogeo = findViewById(R.id.Btn_Logeo)
        usuarioNuevoTxt = findViewById(R.id.UsuarioNuevoTXT)

        btnLogeo.setOnClickListener {
            val gmail = correoLogin.text.toString().trim()
            val contrasena = passLogin.text.toString().trim()

            if (gmail.isEmpty() || contrasena.isEmpty()) {
                Toast.makeText(this, "Por favor, completa todos los campos.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val call = RetrofitClient.apiService.loginUser(gmail, contrasena)
            call.enqueue(object : Callback<Respuesta> {
                override fun onResponse(call: Call<Respuesta>, response: Response<Respuesta>) {
                    if (response.isSuccessful) {
                        val respuesta = response.body()
                        if (respuesta?.success == true && respuesta.usuario != null) {
                            // Guardar usuario en SharedPreferences
                            val sharedPref = getSharedPreferences("usuario", Context.MODE_PRIVATE)
                            with(sharedPref.edit()) {
                                putInt("id", respuesta.usuario.id)
                                putString("gmail", respuesta.usuario.gmail)
                                putString("alias", respuesta.usuario.alias)
                                apply()
                            }

                            Toast.makeText(this@Login, "¡Bienvenido, ${respuesta.usuario.alias}!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@Login, MenuPrincipal::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@Login, respuesta?.message ?: "Error desconocido", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@Login, "Error de respuesta del servidor", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<Respuesta>, t: Throwable) {
                    Toast.makeText(this@Login, "Error: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }

        usuarioNuevoTxt.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return true
    }
}