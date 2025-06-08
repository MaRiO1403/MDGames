package com.example.mdgames

import VJuegoAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
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

class BuscarActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: VJuegoAdapter
    private lateinit var editText: EditText
    private lateinit var botonBusqueda: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_buscar)

        // Views initialization
        editText = findViewById(R.id.editTextText)
        botonBusqueda = findViewById(R.id.searchButton)
        recyclerView = findViewById(R.id.recyclerView)

        // Initialize adapter with empty list and define item click behaviors
        adapter = VJuegoAdapter(
            juegos = mutableListOf(),
            isModifyActivity = false,
            clickAddCarrito = { juego ->
                // Get user session ID, if none, do nothing
                val userId = Sesiones.obtenerSesion(applicationContext) ?: return@VJuegoAdapter
                val cestaDao = Aplicacion.getDatabase(applicationContext).cestaDao()

                // Add or update item quantity in cart asynchronously
                lifecycleScope.launch {
                    val existente = cestaDao.obtenerPorUsuarioYJuego(userId, juego.id)

                    if (existente != null) {
                        // If already in cart, increment quantity
                        val actualizado = existente.copy(cantidad = existente.cantidad + 1)
                        cestaDao.actualizar(actualizado)
                    } else {
                        // If there is no game in the cart, insert it
                        val nuevo = CestaEntity(usuarioId = userId, vjuegoId = juego.id, cantidad = 1)
                        cestaDao.insertar(nuevo)
                    }

                    // Show confirmation toast
                    runOnUiThread {
                        Toast.makeText(this@BuscarActivity, "Añadido al carrito", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onItemClick = { juego ->
                // When a game item is clicked, open its detailed info screen
                val intent = Intent(this, InfoVjActivity::class.java)
                intent.putExtra("vjuego", juego)
                startActivity(intent)
            }
        )

        // Setup RecyclerView with linear layout and adapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Set click listener on search button to trigger search
        botonBusqueda.setOnClickListener {
            val texto = editText.text.toString().trim()
            if (texto.isNotEmpty()) {
                buscarJuegos(texto)
            }
        }
    }

    // Function to query videogames from DB by title asynchronously
    private fun buscarJuegos(texto: String) {
        val dao = Aplicacion.getDatabase(applicationContext).vjuegoDao()

        lifecycleScope.launch {
            val juegosFiltrados = withContext(Dispatchers.IO) {
                dao.buscarPorTitulo(texto)
            }
            adapter.actualizarLista(juegosFiltrados)
        }
    }
}
