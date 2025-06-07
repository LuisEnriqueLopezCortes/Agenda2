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
import android.view.LayoutInflater
import android.widget.EditText
import android.net.Uri
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File

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
        val dialogView = layoutInflater.inflate(R.layout.dialog_editar_nota, null)
        val tituloEdit = dialogView.findViewById<EditText>(R.id.etTitulo)
        val descEdit = dialogView.findViewById<EditText>(R.id.etDescripcion)
        val fechaEdit = dialogView.findViewById<EditText>(R.id.etFecha)

        tituloEdit.setText(nota.titulo)
        descEdit.setText(nota.descripcion)
        fechaEdit.setText(nota.fecha_evento)

        AlertDialog.Builder(this)
            .setTitle("Editar Nota")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val nuevoTitulo = tituloEdit.text.toString()
                val nuevaDescripcion = descEdit.text.toString()
                val nuevaFecha = fechaEdit.text.toString()

                RetrofitClient.apiService.editarNota(
                    nota.id,
                    nuevoTitulo,
                    nuevaDescripcion,
                    nuevaFecha
                ).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        Toast.makeText(this@Listar_Notas, "Nota actualizada", Toast.LENGTH_SHORT).show()
                        cargarNotas(nota.id_usuario) // refrescar
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@Listar_Notas, "Error al actualizar", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun eliminarNota(nota: NotaAgenda) {
        AlertDialog.Builder(this)
            .setTitle("¿Eliminar nota?")
            .setMessage("¿Estás seguro de que deseas eliminar esta nota?")
            .setPositiveButton("Sí") { _, _ ->
                RetrofitClient.apiService.eliminarNota(nota.id).enqueue(object : Callback<Void> {
                    override fun onResponse(call: Call<Void>, response: Response<Void>) {
                        if (response.isSuccessful) {
                            Toast.makeText(this@Listar_Notas, "Nota eliminada", Toast.LENGTH_SHORT).show()
                            listaNotas.remove(nota)
                            adapter.notifyDataSetChanged()
                        } else {
                            Toast.makeText(this@Listar_Notas, "Error al eliminar", Toast.LENGTH_SHORT).show()
                        }
                    }

                    override fun onFailure(call: Call<Void>, t: Throwable) {
                        Toast.makeText(this@Listar_Notas, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}