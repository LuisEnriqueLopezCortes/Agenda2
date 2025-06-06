package com.example.agenda2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MainActivity : AppCompatActivity() {

    private lateinit var btnLogin: Button
    private lateinit var btnRegistro: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)
        btnLogin = findViewById(R.id.Btn_Login)
        btnRegistro = findViewById(R.id.Btn_Registro)

        btnLogin.setOnClickListener {
            startActivity(Intent(this, Login::class.java))
        }

        btnRegistro.setOnClickListener {
            startActivity(Intent(this, Registro::class.java))
        }
    }
}