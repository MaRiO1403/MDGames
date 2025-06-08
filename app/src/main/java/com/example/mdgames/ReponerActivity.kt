package com.example.mdgames

import VJuegoAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mdgames.entity.VJuegoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ReponerActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VJuegoAdapter
    private lateinit var db: AppDatabase
    private val juegosOcultos = mutableListOf<VJuegoEntity>() // List of hidden games (not available)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar) // Reusing the same layout as ModificarActivity

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the adapter with empty list and item click listener to show the restore dialog
        adapter = VJuegoAdapter(
            juegos = mutableListOf(),
            clickAddCarrito = {},  // Cart button not used in this context
            isModifyActivity = true,  // Indicates this is a management view
            onItemClick = { juego ->
                // Show confirmation dialog to restore the game
                mostrarDialogoReponer(juego)
            }
        )
        recyclerView.adapter = adapter

        db = Aplicacion.getDatabase(this)

        // Load hidden games from Room database
        cargarJuegosOcultosDesdeRoom()
    }

    // Load games where disponible == false (hidden)
    private fun cargarJuegosOcultosDesdeRoom() {
        lifecycleScope.launch {
            val juegos = withContext(Dispatchers.IO) {
                db.vjuegoDao().obtenerJuegosOcultos()  // Consultamos los juegos con disponible = 0
            }

            juegosOcultos.clear()
            juegosOcultos.addAll(juegos)

            runOnUiThread {
                if (::adapter.isInitialized) {
                    // Update existing adapter's list
                    adapter.actualizarLista(juegosOcultos)
                } else {
                    // If adapter wasn't initialized for some reason (fallback)
                    adapter = VJuegoAdapter(
                        juegos = juegosOcultos,
                        clickAddCarrito = {},
                        isModifyActivity = true,
                        onItemClick = { juego ->

                            mostrarDialogoReponer(juego)
                        }
                    )
                    recyclerView.adapter = adapter
                }
            }
        }
    }

    // Show a confirmation dialog before restoring a game
    private fun mostrarDialogoReponer(juego: VJuegoEntity) {
        val dialog = AlertDialog.Builder(this)
            .setTitle("Reponer Videojuego")
            .setMessage("¿Estás seguro de que quieres reponer el videojuego ${juego.titulo}?")
            .setPositiveButton("Sí") { dialogInterface, _ ->
                reponerJuego(juego)
                dialogInterface.dismiss()
            }
            .setNegativeButton("No") { dialogInterface, _ ->
                dialogInterface.dismiss()
            }
            .create()

        dialog.show()
    }

    // Mark a hidden game as available again (disponible = true)
    private fun reponerJuego(juego: VJuegoEntity) {
        lifecycleScope.launch {
            val juegoRepuesto = juego.copy(disponible = true)

            withContext(Dispatchers.IO) {
                db.vjuegoDao().actualizarVJuego(juegoRepuesto)
            }

            runOnUiThread {
                Toast.makeText(this@ReponerActivity, "El videojuego ${juego.titulo} se ha repuesto", Toast.LENGTH_SHORT).show()
                // Reload the list to remove restored game
                cargarJuegosOcultosDesdeRoom()
            }
        }
    }
}