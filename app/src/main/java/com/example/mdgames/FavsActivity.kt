package com.example.mdgames

import VJuegoAdapter
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mdgames.ManejoDeSesiones.Sesiones
import com.example.mdgames.entity.CestaEntity
import com.example.mdgames.entity.FavoritoEntity
import com.example.mdgames.entity.VJuegoEntity
import kotlinx.coroutines.launch

class FavsActivity : AppCompatActivity() {

    private lateinit var adapter: VJuegoAdapter
    private val juegosFavoritos = mutableListOf<VJuegoEntity>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favs)

        // Initialize RecyclerView and adapter
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        adapter = VJuegoAdapter(
            juegosFavoritos,
            isModifyActivity = false,
            clickAddCarrito = { juego -> agregarAJuegosCesta(juego) }, // Callback to add game to cart
            onItemClick = { juego -> mostrarDialogoQuitarFavorito(juego) } // Callback to remove favorite
        )
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Load user's favorite games from database
        cargarFavoritos()
    }

    private fun cargarFavoritos() {
        val userId = Sesiones.obtenerSesion(applicationContext) ?: return
        val db = Aplicacion.getDatabase(applicationContext)
        val favoritoDao = db.favoritoDao()
        val vjuegoDao = db.vjuegoDao()

        lifecycleScope.launch {
            // Get all favorite entries for the user
            val favoritos = favoritoDao.obtenerTodosLosFavoritos(userId)
            // Map each favorite to the actual game entity
            val juegos = favoritos.mapNotNull { vjuegoDao.obtenerJuegoPorId(it.vjuegoId) }

            juegosFavoritos.clear()
            juegosFavoritos.addAll(juegos)
            runOnUiThread { adapter.notifyDataSetChanged() } // Update UI
        }
    }

    private fun mostrarDialogoQuitarFavorito(juego: VJuegoEntity) {
        AlertDialog.Builder(this)
            .setTitle("Quitar de favoritos")
            .setMessage("¿Quieres quitar '${juego.titulo}' de favoritos?")
            .setPositiveButton("Sí") { _, _ -> eliminarDeFavoritos(juego) }
            .setNegativeButton("No", null)
            .show()
    }

    private fun eliminarDeFavoritos(juego: VJuegoEntity) {
        val userId = Sesiones.obtenerSesion(applicationContext) ?: return
        val favorito = FavoritoEntity(usuarioId = userId, vjuegoId = juego.id)

        lifecycleScope.launch {
            // Remove the favorite entry from the database
            Aplicacion.getDatabase(applicationContext).favoritoDao().borrarFavorito(favorito)
            juegosFavoritos.remove(juego) // Remove from local list
            runOnUiThread { adapter.notifyDataSetChanged() } // Update UI
        }
    }

    private fun agregarAJuegosCesta(juego: VJuegoEntity) {
        val userId = Sesiones.obtenerSesion(applicationContext) ?: return
        val cestaDao = Aplicacion.getDatabase(applicationContext).cestaDao()

        lifecycleScope.launch {
            // Check if the game is already in the cart
            val existente = cestaDao.obtenerPorUsuarioYJuego(userId, juego.id)

            if (existente != null) {
                // If it exists, increase quantity by 1
                val actualizado = existente.copy(cantidad = existente.cantidad + 1)
                cestaDao.actualizar(actualizado)
            } else {
                // If not, add as a new cart entry with quantity 1
                val nuevo = CestaEntity(usuarioId = userId, vjuegoId = juego.id, cantidad = 1)
                cestaDao.insertar(nuevo)
            }

            runOnUiThread {
                Toast.makeText(this@FavsActivity, "Añadido al carrito", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

