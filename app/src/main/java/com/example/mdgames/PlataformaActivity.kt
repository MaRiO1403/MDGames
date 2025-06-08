package com.example.mdgames

import VJuegoAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mdgames.ManejoDeSesiones.Sesiones
import com.example.mdgames.entity.CestaEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PlataformaActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VJuegoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificar) // Reusing the same layout as ModificarActivity

        // Get the selected platform from the intent
        val plataformaSeleccionada = intent.getStringExtra("platform") ?: return

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Set up the adapter with click listeners
        adapter = VJuegoAdapter(
            juegos = mutableListOf(),
            isModifyActivity = false, // We're not in modify mode
            clickAddCarrito = { juego ->
                // Get the current user ID from session
                val userId = Sesiones.obtenerSesion(applicationContext) ?: return@VJuegoAdapter
                // Access the shopping cart DAO
                val cestaDao = Aplicacion.getDatabase(applicationContext).cestaDao()

                lifecycleScope.launch {
                    // Check if the game is already in the cart
                    val existente = cestaDao.obtenerPorUsuarioYJuego(userId, juego.id)

                    if (existente != null) {
                        // If it exists, increment the quantity
                        val actualizado = existente.copy(cantidad = existente.cantidad + 1)
                        cestaDao.actualizar(actualizado)
                    } else {
                        // If not, create a new cart entry
                        val nuevo = CestaEntity(usuarioId = userId, vjuegoId = juego.id, cantidad = 1)
                        cestaDao.insertar(nuevo)
                    }

                    // Notify the user
                    runOnUiThread {
                        Toast.makeText(this@PlataformaActivity, "Añadido al carrito", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onItemClick = { juego ->
                // When an item is clicked, go to InfoVjActivity with the game info
                val intent = Intent(this, InfoVjActivity::class.java)
                intent.putExtra("vjuego", juego)
                startActivity(intent)
            }
        )


        recyclerView.adapter = adapter

        val db = Aplicacion.getDatabase(this)
        val vjuegoDao = db.vjuegoDao()

        // Load games for the selected platform in a background thread
        lifecycleScope.launch {
            val juegosFiltrados = withContext(Dispatchers.IO) {
                vjuegoDao.obtenerPorPlataforma(plataformaSeleccionada)
            }

            // Update the adapter with the filtered list
            adapter.actualizarLista(juegosFiltrados)
        }
    }
}

