package com.example.agenda2.ListarNotas

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.agenda2.R
import com.example.agenda2.model.NotaAgenda
import com.example.agenda2.network.RetrofitClient
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.agenda2.adapter.NotasAdapter
import com.example.agenda2.network.ApiService

class Listar_Notas : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: NotasAdapter
    private val listaNotas = mutableListOf<NotaAgenda>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_listar_notas)

        recyclerView = findViewById(R.id.recyclerNotas)
        adapter = NotasAdapter(listaNotas) { nota -> mostrarMenu(nota) }
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        val idUsuario = getSharedPreferences("usuario", MODE_PRIVATE).getInt("id", -1)
        if (idUsuario != -1) cargarNotas(idUsuario)
    }

    private fun cargarNotas(id: Int) {
        RetrofitClient.apiService.getNotasUsuario(id).enqueue(object : Callback<List<NotaAgenda>> {
            override fun onResponse(call: Call<List<NotaAgenda>>, response: Response<List<NotaAgenda>>) {
                response.body()?.let {
                    listaNotas.clear()
                    listaNotas.addAll(it)
                    adapter.notifyDataSetChanged()
                }
            }

            override fun onFailure(call: Call<List<NotaAgenda>>, t: Throwable) {
                Toast.makeText(this@Listar_Notas, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun mostrarMenu(nota: NotaAgenda) {
        val opciones = arrayOf("Editar", "Eliminar")
        AlertDialog.Builder(this)
            .setTitle("Selecciona una acción")
            .setItems(opciones) { _, which ->
                when (which) {
                    0 -> editarNota(nota)
                    1 -> eliminarNota(nota)
                }
            }
            .show()
    }

    private fun editarNota(nota: NotaAgenda) {
        // TODO: Abrir nueva activity o mostrar dialogo para editar
    }

    private fun eliminarNota(nota: NotaAgenda) {
        // TODO: Hacer llamada DELETE al backend y refrescar lista
    }
}