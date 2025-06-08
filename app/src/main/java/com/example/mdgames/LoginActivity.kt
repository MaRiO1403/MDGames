package com.example.mdgames

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mdgames.Dao.UserDao
import com.example.mdgames.ManejoDeSesiones.Sesiones
import com.example.mdgames.entity.UserEntity
import kotlinx.coroutines.launch

class LoginActivity : AppCompatActivity() {

    private lateinit var viewFlipper: ViewFlipper
    private lateinit var userDao: UserDao

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        viewFlipper = findViewById(R.id.viewFlipper)

        val db = Aplicacion.getDatabase(applicationContext)

        userDao = db.userDao()

        // Login screen elements
        val editEmail = findViewById<EditText>(R.id.editEmail)
        val editPassword = findViewById<EditText>(R.id.editPassword)
        val botonLogin = findViewById<Button>(R.id.botonLogin)
        val textRegister = findViewById<TextView>(R.id.textRegister)

        // Register screen elements
        val editName = findViewById<EditText>(R.id.editName)
        val editEmailRegister = findViewById<EditText>(R.id.editEmailRegister)
        val editPasswordRegister = findViewById<EditText>(R.id.editPasswordRegister)
        val editPasswordRegister2 = findViewById<EditText>(R.id.editPasswordRegister2)
        val botonRegister = findViewById<Button>(R.id.botonRegister)
        val textBackToLogin = findViewById<TextView>(R.id.textBackToLogin)

        // Switch to register view with animation
        textRegister.setOnClickListener {
            viewFlipper.setInAnimation(this, R.anim.slide_in_right)
            viewFlipper.setOutAnimation(this, R.anim.slide_out_left)
            viewFlipper.showNext()
        }

        // Switch back to login view with animation
        textBackToLogin.setOnClickListener {
            viewFlipper.setInAnimation(this, R.anim.slide_in_left)
            viewFlipper.setOutAnimation(this, R.anim.slide_out_right)
            viewFlipper.showPrevious()
        }

        // Handle login button click
        botonLogin.setOnClickListener {
            val email = editEmail.text.toString()
            val password = editPassword.text.toString()

            lifecycleScope.launch {
                // Try to get user from DB matching email and password
                val user = userDao.obtenerUsuario(email, password)
                runOnUiThread {
                    if (user != null) {
                        // Save session and navigate depending on admin status
                        Sesiones.guardarSesion(applicationContext, user.id)
                        Toast.makeText(this@LoginActivity, "Bienvenido, ${user.nombre}", Toast.LENGTH_SHORT).show()
                        val intent = if (user.isAdmin) {
                            Intent(this@LoginActivity, AdminActivity::class.java)
                        } else {
                            Intent(this@LoginActivity, MainActivity::class.java)
                        }
                        intent.putExtra("usuarioId", user.id)
                        startActivity(intent)
                        finish()
                    } else {
                        // Show error message if login fails
                        Toast.makeText(this@LoginActivity, "Email o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Handle register button click
        botonRegister.setOnClickListener {
            val name = editName.text.toString()
            val email = editEmailRegister.text.toString()
            val password = editPasswordRegister.text.toString()
            val password2 = editPasswordRegister2.text.toString()

            // Validate that all fields are filled
            if (name.isBlank() || email.isBlank() || password.isBlank() || password2.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if passwords match
            if (password != password2) {
                Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check email domain
            if (!email.endsWith("@gmail.com")) {
                Toast.makeText(this, "El correo debe terminar en @gmail.com", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            lifecycleScope.launch {
                // Check if email already exists
                val exists = userDao.obtenerUsuarioEmail(email)
                if (exists != null) {
                    runOnUiThread {
                        Toast.makeText(this@LoginActivity, "Este correo ya está registrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Insert new user in DB
                    val newUser = UserEntity(nombre = name, email = email, contrasena = password)
                    userDao.insertUser(newUser)
                    runOnUiThread {
                        // Switch back to login view
                        Toast.makeText(this@LoginActivity, "Usuario registrado", Toast.LENGTH_SHORT).show()
                        viewFlipper.setInAnimation(this@LoginActivity, R.anim.slide_in_left)
                        viewFlipper.setOutAnimation(this@LoginActivity, R.anim.slide_out_right)
                        viewFlipper.showPrevious()

                        // Clear registration form
                        editName.text.clear()
                        editEmailRegister.text.clear()
                        editPasswordRegister.text.clear()
                        editPasswordRegister2.text.clear()

                    }
                }
            }
        }
    }
}