package com.example.mdgames

import VJuegoAdapter
import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mdgames.entity.VJuegoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ModificarActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VJuegoAdapter
    private lateinit var db: AppDatabase
    private val juegos = mutableListOf<VJuegoEntity>()

    // Launcher that receives result from the edit activity
    private val editarLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (it.resultCode == RESULT_OK) {
            // Reload data after editing
            cargarJuegosDesdeRoom()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Assign an empty adapter initially
        adapter = VJuegoAdapter(
            juegos = mutableListOf(),
            clickAddCarrito = {},
            isModifyActivity = true,
            onItemClick = { juego ->
                val intent = Intent(this@ModificarActivity, ModificarVjActivity::class.java)
                intent.putExtra("vjuego", juego)
                editarLauncher.launch(intent)
            }
        )
        recyclerView.adapter = adapter // Set empty adapter initially

        db = Aplicacion.getDatabase(this)

        // Call to load the list of games from Room
        cargarJuegosDesdeRoom()
    }


    private fun cargarJuegosDesdeRoom() {
        lifecycleScope.launch {
            val nuevosJuegos = withContext(Dispatchers.IO) {
                db.vjuegoDao().obtenerTodosLosVJuegos()
            }

            juegos.clear()
            juegos.addAll(nuevosJuegos)

            // If adapter is already initialized, update the list, otherwise create a new one
            runOnUiThread {
                if (::adapter.isInitialized) {
                    // Update the list with the new games
                    adapter.actualizarLista(juegos)
                } else {
                    // Initialize the adapter for the first time
                    adapter = VJuegoAdapter(
                        juegos = juegos,
                        clickAddCarrito = {}, // Not used in this activity
                        isModifyActivity = true,
                        onItemClick = { juego ->
                            val intent = Intent(this@ModificarActivity, ModificarVjActivity::class.java)
                            intent.putExtra("vjuego", juego)
                            editarLauncher.launch(intent)
                        }
                    )
                    recyclerView.adapter = adapter // Assign the adapter
                }
            }
        }
    }
}