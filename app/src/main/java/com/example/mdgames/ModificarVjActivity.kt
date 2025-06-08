package com.example.mdgames

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mdgames.entity.VJuegoEntity
import kotlinx.coroutines.launch

class ModificarVjActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_modificarvj)

        val editTitle = findViewById<EditText>(R.id.editTitle)
        val editDescription = findViewById<EditText>(R.id.editDescription)
        val editCover = findViewById<EditText>(R.id.editCover)
        val editBackground = findViewById<EditText>(R.id.editBackground)
        val editPrice = findViewById<EditText>(R.id.editPrice)
        val editPlatform = findViewById<EditText>(R.id.editPlatform)
        val modifyButton = findViewById<Button>(R.id.createButton)
        val hideButton = findViewById<Button>(R.id.ButtonOcultar)

        val videojuego = intent.getSerializableExtra("vjuego") as? VJuegoEntity

        videojuego?.let {
            // Fill in the fields with the current data of the game
            editTitle.setText(it.titulo)
            editDescription.setText(it.descripcion)
            editCover.setText(it.caratulaUrl)
            editBackground.setText(it.imagenUrl)
            editPrice.setText(it.precio.toString())
            editPlatform.setText(it.plataforma)

            // Disable fields that should not be modified
            val camposNoModificables = listOf(editTitle, editDescription, editCover, editBackground, editPlatform)
            camposNoModificables.forEach { field ->
                field.isEnabled = false
                field.setTextColor(Color.GRAY)
                field.setBackgroundColor(Color.TRANSPARENT)
            }
        }

        val db = Aplicacion.getDatabase(this)
        val dao = db.vjuegoDao()

        modifyButton.setOnClickListener {
            // Validate price
            val nuevoPrecio = editPrice.text.toString().trim().toIntOrNull()
            if (nuevoPrecio == null) {
                Toast.makeText(this, "Precio inválido", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            videojuego?.let {
                // Update the game with the new price
                val juegoActualizado = it.copy(precio = nuevoPrecio)
                lifecycleScope.launch {
                    dao.actualizarVJuego(juegoActualizado)
                    runOnUiThread {
                        // Notify that the price has been updated
                        Toast.makeText(this@ModificarVjActivity, "Precio actualizado", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK) // Indicates that the edit was successful
                        finish() // Close the activity
                    }
                }
            }
        }

        hideButton.setOnClickListener {
            videojuego?.let {
                // Mark the game as unavailable
                val juegoOculto = it.copy(disponible = false)
                lifecycleScope.launch {
                    dao.actualizarVJuego(juegoOculto)
                    runOnUiThread {
                        // Notify that the game has been hidden
                        Toast.makeText(this@ModificarVjActivity, "Juego ocultado", Toast.LENGTH_SHORT).show()
                        setResult(RESULT_OK)
                        finish() // Close the activity
                    }
                }
            }
        }
    }
}