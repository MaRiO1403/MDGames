package com.example.mdgames

import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.example.mdgames.ManejoDeSesiones.Sesiones
import com.example.mdgames.entity.CestaEntity
import com.example.mdgames.entity.FavoritoEntity
import com.example.mdgames.entity.VJuegoEntity
import kotlinx.coroutines.launch

class InfoVjActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_infovj)

        // Receive the game object passed from previous activity via Intent
        val juego = intent.getSerializableExtra("vjuego") as? VJuegoEntity ?: return

        val tvTitulo = findViewById<TextView>(R.id.tvTitulo)
        val tvPlataforma = findViewById<TextView>(R.id.tvPlataforma)
        val tvPrecio = findViewById<TextView>(R.id.tvPrecio)
        val tvDescripcion = findViewById<TextView>(R.id.tvDescripcion)
        val tvImagen = findViewById<ImageView>(R.id.tvImage)

        // Assign game data to the views
        tvTitulo.text = juego.titulo
        tvPlataforma.text = juego.plataforma
        tvPrecio.text = "${juego.precio}€"
        tvDescripcion.text = juego.descripcion

        // Load game image using Glide library
        Glide.with(this)
            .load(juego.imagenUrl)
            .into(tvImagen)

        val addToCart = findViewById<Button>(R.id.botonAddCarrito)
        val favButton = findViewById<ImageButton>(R.id.botonFav)

        // Handle "Add to Cart" button click
        addToCart.setOnClickListener {
            val userId = Sesiones.obtenerSesion(applicationContext) ?: return@setOnClickListener
            val cestaDao = Aplicacion.getDatabase(applicationContext).cestaDao()
            val vjuegoId = juego.id

            lifecycleScope.launch {
                // Check if the game is already in the cart
                val existente = cestaDao.obtenerPorUsuarioYJuego(userId, vjuegoId)

                if (existente != null) {
                    // If it exists, increment quantity by 1
                    val actualizado = existente.copy(cantidad = existente.cantidad + 1)
                    cestaDao.actualizar(actualizado)
                } else {
                    // If not, create a new cart entry with quantity 1 (default
                    val nuevo = CestaEntity(usuarioId = userId, vjuegoId = vjuegoId)
                    cestaDao.insertar(nuevo)
                }

                runOnUiThread {
                    Toast.makeText(this@InfoVjActivity, "Añadido al carrito", Toast.LENGTH_SHORT).show()
                }
            }
        }


        // Initialize favorite button status based on whether game is already favorite
        val userId = Sesiones.obtenerSesion(applicationContext)
        val favoritoDao = Aplicacion.getDatabase(applicationContext).favoritoDao()

        lifecycleScope.launch {
            val favoritos = favoritoDao.obtenerTodosLosFavoritos(userId!!)
            val yaExiste = favoritos.any { it.vjuegoId == juego.id }

            // Update favorite icon accordingly
            runOnUiThread {
                if (yaExiste) {
                    favButton.setImageResource(R.drawable.fav)
                } else {
                    favButton.setImageResource(R.drawable.fav_border)
                }
            }
        }

        // Handle favorite button clicks to add or remove from favorites
        favButton.setOnClickListener {
            val userId = Sesiones.obtenerSesion(applicationContext) ?: return@setOnClickListener
            val favoritoDao = Aplicacion.getDatabase(applicationContext).favoritoDao()
            val favorito = FavoritoEntity(usuarioId = userId, vjuegoId = juego.id)

            lifecycleScope.launch {
                val favoritos = favoritoDao.obtenerTodosLosFavoritos(userId)
                val yaExiste = favoritos.any { it.vjuegoId == juego.id }

                if (!yaExiste) {
                    // If not already favorite, add it
                    favoritoDao.agregarFavorito(favorito)
                    runOnUiThread {
                        favButton.setImageResource(R.drawable.fav)
                        Toast.makeText(this@InfoVjActivity, "Añadido a favoritos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // If already favorite, remove it
                    favoritoDao.borrarFavorito(favorito)
                    runOnUiThread {
                        favButton.setImageResource(R.drawable.fav_border)
                        Toast.makeText(this@InfoVjActivity, "Eliminado de favoritos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}