package com.example.mdgames

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton

class AdminActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin)

        // Get references to the admin buttons
        val botonAdd = findViewById<MaterialButton>(R.id.botonAddVJuego) // Add new game
        val botonModify = findViewById<MaterialButton>(R.id.botonModifVJuego) // Modify existing game
        val botonReponer = findViewById<MaterialButton>(R.id.botonReponerVJuego) // Restock game
        val botonLogout = findViewById<MaterialButton>(R.id.botonLogout) // Log out button

        // Go to the screen to add a new video game
        botonAdd.setOnClickListener {
            startActivity(Intent(this, AddVjuegoActivity::class.java))
        }

        // Go to the screen to modify a video game
        botonModify.setOnClickListener {
            startActivity(Intent(this, ModificarActivity::class.java))
        }

        // Go to the screen to restock a game
        botonReponer.setOnClickListener {
            startActivity(Intent(this, ReponerActivity::class.java))
        }

        // Log out: clear activity history and return to login
        botonLogout.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
    }
}