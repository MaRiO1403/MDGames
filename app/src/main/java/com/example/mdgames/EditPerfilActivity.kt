package com.example.mdgames

import android.graphics.Color
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.mdgames.ManejoDeSesiones.Sesiones
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditPerfilActivity : AppCompatActivity() {

    private lateinit var email: EditText
    private lateinit var nombreUsuario: EditText
    private lateinit var passwActual: EditText
    private lateinit var newPassw: EditText
    private lateinit var newPassw2: EditText
    private lateinit var botonCambNom: Button
    private lateinit var botonActPassw: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_editperfil)

        // Initialize views
        email = findViewById(R.id.editEmail)
        nombreUsuario = findViewById(R.id.editNombreUsuario)
        passwActual = findViewById(R.id.editPasswActual)
        newPassw = findViewById(R.id.editNewPassw)
        newPassw2 = findViewById(R.id.editNewPassw2)
        botonCambNom = findViewById(R.id.btnCambiarNombre)
        botonActPassw = findViewById(R.id.btnActualizarContrasena)

        // Get current user ID from session
        val userId = Sesiones.obtenerSesion(this)

        // If no valid session, notify and close activity
        if (userId == -1) {
            Toast.makeText(this, "Sesión no válida", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        val db = AppDatabase.getInstance(this)
        val userDao = db.userDao()

        // Load user data from DB in a coroutine
        lifecycleScope.launch {
            val usuario = withContext(Dispatchers.IO) {
                userDao.obtenerUsuarioPorId(userId)
            }

            usuario?.let {
                // Set email field (read-only)
                email.setText(it.email)
                email.isEnabled = false
                email.setTextColor(Color.GRAY)
                email.setBackgroundColor(Color.TRANSPARENT)

                // Set username field
                nombreUsuario.setText(it.nombre)
            }
        }

        // Change username button click handler
        botonCambNom.setOnClickListener {
            val nuevoNombre = nombreUsuario.text.toString().trim()
            if (nuevoNombre.isEmpty()) {
                Toast.makeText(this, "El nombre no puede estar vacío", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Update username in database on background thread
            lifecycleScope.launch(Dispatchers.IO) {
                val usuario = userDao.obtenerUsuarioPorId(userId)
                if (usuario != null) {
                    usuario.nombre = nuevoNombre
                    userDao.actualizarUsuario(usuario)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditPerfilActivity, "Nombre de usuario actualizado", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        // Update password button click handler
        botonActPassw.setOnClickListener {
            val actual = passwActual.text.toString()
            val nueva = newPassw.text.toString()
            val repetir = newPassw2.text.toString()

            // Validate input fields are not empty
            if (actual.isBlank() || nueva.isBlank() || repetir.isBlank()) {
                Toast.makeText(this, "Completa todos los campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check that new passwords match
            if (nueva != repetir) {
                Toast.makeText(this, "Las nuevas contraseñas no coinciden", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Prevent new password being same as current
            if (actual == nueva) {
                Toast.makeText(this, "La nueva contraseña no puede ser igual a la actual", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verify current password and update to new password
            lifecycleScope.launch(Dispatchers.IO) {
                val usuario = userDao.obtenerUsuarioPorId(userId)
                if (usuario != null && usuario.contrasena == actual) {
                    usuario.contrasena = nueva
                    userDao.actualizarUsuario(usuario)
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditPerfilActivity, "Contraseña actualizada correctamente", Toast.LENGTH_SHORT).show()
                        // Clear password input fields after success
                        passwActual.text.clear()
                        newPassw.text.clear()
                        newPassw2.text.clear()
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@EditPerfilActivity, "Contraseña actual incorrecta", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }
}
