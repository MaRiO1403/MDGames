package com.example.mdgames

import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mdgames.entity.VJuegoEntity
import kotlinx.coroutines.launch

class AddVjuegoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_addvjuego)

        // Get database and DAO instance
        val db = Aplicacion.getDatabase(this)
        val dao = db.vjuegoDao()

        // Get references to input fields and button
        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editDescription = findViewById<EditText>(R.id.editDescription)
        val editCover = findViewById<EditText>(R.id.editCover)
        val editBackground = findViewById<EditText>(R.id.editBackground)
        val editPrice = findViewById<EditText>(R.id.editPrice)
        val editPlatform = findViewById<EditText>(R.id.editPlatform)
        val createButton = findViewById<Button>(R.id.createButton)

        // Get text from input fields and trim whitespace
        createButton.setOnClickListener {
            val titulo = editTitle.text.toString().trim()
            val descripcion = editDescription.text.toString().trim()
            val caratula = editCover.text.toString().trim()
            val fondo = editBackground.text.toString().trim()
            val precioTexto = editPrice.text.toString().trim()
            val plataforma = editPlatform.text.toString().trim()

            // List of valid platforms
            val plataformasValidas = listOf("PlayStation 5", "Xbox Series X|S", "Nintendo Switch", "PlayStation 4", "Steam")

            // Validate required fields
            if (titulo.isEmpty() || descripcion.isEmpty() || precioTexto.isEmpty() || plataforma.isEmpty()) {
                Toast.makeText(this, "Completa todos los campos obligatorios", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate platform
            if (!plataformasValidas.contains(plataforma)) {
                Toast.makeText(this, "Plataforma no disponible", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate price is a number
            val precio = precioTexto.toIntOrNull()
            if (precio == null) {
                Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Launch coroutine to access database in background
            lifecycleScope.launch {
                // Check if a game with same title and platform already exists
                val existente = dao.obtenerPorTituloYPlataforma(titulo, plataforma)

                if (existente != null) {
                    // If game already exists, show message
                    runOnUiThread {
                        Toast.makeText(
                            this@AddVjuegoActivity,
                            "Ese juego ya existe para esa plataforma",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Create new game object
                    val nuevoJuego = VJuegoEntity(
                        titulo = titulo,
                        descripcion = descripcion,
                        esFavorita = false,
                        caratulaUrl = caratula,
                        imagenUrl = fondo,
                        precio = precio,
                        plataforma = plataforma,
                        disponible = true
                    )
                    // Insert the new game into the database
                    dao.agregarVJuego(nuevoJuego)

                    // Show success message and close the activity
                    runOnUiThread {
                        Toast.makeText(this@AddVjuegoActivity, "Juego añadido correctamente", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }
            }
        }
    }
}