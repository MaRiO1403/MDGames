package com.example.mdgames

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.mdgames.ManejoDeSesiones.Sesiones
import com.example.mdgames.Dao.UserDao
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private var menuVisible = false

    private lateinit var sideMenu: View
    private lateinit var textViewNombrePerfil: TextView
    private lateinit var userDao: UserDao
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        // References to views
        sideMenu = findViewById(R.id.sideMenu)
        textViewNombrePerfil = sideMenu.findViewById(R.id.tvNombre)

        // Get database and user DAO
        val db = AppDatabase.getInstance(this)
        userDao = db.userDao()

        // Get current logged-in user ID
        userId = Sesiones.obtenerSesion(this)

        // Load user's name into the side menu
        cargarNombreUsuario()

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val botonLupa = findViewById<ImageButton>(R.id.botonLupa)
        val botonPerfil = findViewById<ImageButton>(R.id.botonPerfil)
        val botonCarrito = findViewById<ImageButton>(R.id.botonCarrito)
        val botonFav = findViewById<ImageButton>(R.id.botonFav)
        val overlay = findViewById<View>(R.id.overlay)

        // On search button click, open BuscarActivity
        botonLupa.setOnClickListener {
            startActivity(Intent(this, BuscarActivity::class.java))
        }

        // On cart button click, open CarritoActivity
        botonCarrito.setOnClickListener {
            startActivity(Intent(this, CarritoActivity::class.java))
        }

        // On favorites button click, open FavsActivity
        botonFav.setOnClickListener {
            startActivity(Intent(this, FavsActivity::class.java))
        }

        // On profile button click, toggle side menu visibility with animation
        botonPerfil.setOnClickListener {
            if (!menuVisible) {
                val slideIn = AnimationUtils.loadAnimation(this, R.anim.slide_in_right)
                sideMenu.startAnimation(slideIn)
                sideMenu.visibility = View.VISIBLE
                findViewById<View>(R.id.overlay).visibility = View.VISIBLE
                menuVisible = true
            } else {
                cerrarMenu()
            }
        }
        // On overlay click, close menu if visible
        overlay.setOnClickListener {
            if (menuVisible) {
                cerrarMenu()
            }
        }
        // Consume touch events on side menu to prevent clicks from closing it
        sideMenu.setOnTouchListener { v, event -> true}

        // Buttons inside the side menu
        val btnMisPedidos = sideMenu.findViewById<Button>(R.id.btnMisPedidos)
        val btnEditarPerfil = sideMenu.findViewById<Button>(R.id.btnEditarPerfil)
        val btnCerrarSesion = sideMenu.findViewById<Button>(R.id.btnCerrarSesion)

        // Open PedidosActivity and close menu
        btnMisPedidos.setOnClickListener {
            startActivity(Intent(this, PedidosActivity::class.java))
            cerrarMenu()
        }

        // Open EditPerfilActivity and close menu
        btnEditarPerfil.setOnClickListener {
            startActivity(Intent(this, EditPerfilActivity::class.java))
            cerrarMenu()
        }

        // Log out, clear activity stack and open LoginActivity
        btnCerrarSesion.setOnClickListener {
            Sesiones.cerrarSesion(this)
            val intent = Intent(this, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            finish()
        }

        // Game platform buttons
        val botonPS5 = findViewById<View>(R.id.botonPS5)
        val botonNS = findViewById<View>(R.id.botonNS)
        val botonXbox = findViewById<View>(R.id.botonXbox)
        val botonSteam = findViewById<View>(R.id.botonSteam)
        val botonPS4 = findViewById<View>(R.id.botonPS4)

        val platformButtons = listOf(botonPS5, botonNS, botonXbox, botonSteam, botonPS4)

        // On platform button click, open PlataformaActivity passing the platform name
        for (button in platformButtons) {
            button.setOnClickListener {
                val platformName = it.tag as? String ?: "Unknown"
                val intent = Intent(this, PlataformaActivity::class.java)
                intent.putExtra("platform", platformName)
                startActivity(intent)
            }
        }

    }

    // Reload user's name every time activity resumes to update UI
    override fun onResume() {
        super.onResume()
        cargarNombreUsuario()
    }

    // Function to load user's name from DB and display it in the menu
    private fun cargarNombreUsuario() {
        lifecycleScope.launch {
            val user = userDao.obtenerUsuarioPorId(userId)
            runOnUiThread {
                if (user != null) {
                    textViewNombrePerfil.text = user.nombre
                } else {
                    textViewNombrePerfil.text = "Perfil"
                }
            }
        }
    }

    // Function to close the side menu with animation
    private fun cerrarMenu() {
        val slideOut = AnimationUtils.loadAnimation(this, R.anim.slide_out_right)
        sideMenu.startAnimation(slideOut)
        sideMenu.postDelayed({
            sideMenu.visibility = View.GONE
            findViewById<View>(R.id.overlay).visibility = View.GONE
            menuVisible = false
        }, 300)
    }
}