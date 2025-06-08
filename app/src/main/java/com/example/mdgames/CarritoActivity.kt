package com.example.mdgames

import CarritoAdapter
import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mdgames.ManejoDeSesiones.Sesiones
import com.example.mdgames.entity.CestaConVjuego
import com.example.mdgames.entity.DetallePedidoEntity
import com.example.mdgames.entity.PedidoEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class CarritoActivity : AppCompatActivity() {

    private lateinit var recyclerCarrito: RecyclerView
    private lateinit var carritoAdapter: CarritoAdapter
    private lateinit var tvTotal: TextView
    private lateinit var btnRealizarCompra: Button

    private var listaCesta: MutableList<CestaConVjuego> = mutableListOf()
    private var userId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        // Initialize views
        recyclerCarrito = findViewById(R.id.recyclerCarrito)
        tvTotal = findViewById(R.id.tvTotal)
        btnRealizarCompra = findViewById(R.id.btnRealizarCompra)

        // Get current logged user ID from session
        userId = Sesiones.obtenerSesion(applicationContext) ?: return

        // Initialize adapter with callback functions for increment, decrement and remove item
        carritoAdapter = CarritoAdapter(
            items = listaCesta,
            onIncrement = { cestaEntity ->
                val item = listaCesta.find { it.cesta == cestaEntity }
                item?.let {
                    // Increase quantity by 1
                    it.cesta.cantidad += 1
                    guardarCambiosEnDB(it)
                    actualizarTotal()
                    carritoAdapter.notifyDataSetChanged()
                }
            },
            onDecrement = { cestaEntity ->
                val item = listaCesta.find { it.cesta == cestaEntity }
                item?.let {
                    if (it.cesta.cantidad > 1) {
                        // Decrease quantity by 1 if quantity > 1
                        it.cesta.cantidad -= 1
                        guardarCambiosEnDB(it)
                        actualizarTotal()
                        carritoAdapter.notifyDataSetChanged()
                    }
                }
            },
            onRemove = { cestaEntity ->
                val item = listaCesta.find { it.cesta == cestaEntity }
                item?.let {
                    eliminarDeCarrito(it)
                }
            }
        )

        // Set RecyclerView layout manager and adapter
        recyclerCarrito.apply {
            layoutManager = LinearLayoutManager(this@CarritoActivity)
            adapter = carritoAdapter
        }

        // Function to randomly calculate discount
        fun calcularDescuento(totalOriginal: Int): Pair<Int, Int> {
            val aplicarDescuento = (1..100).random() <= 35  // 35% chance to apply discount

            if (aplicarDescuento) {
                val tipoDescuento = (1..100).random()
                val descuento = if (tipoDescuento <= 75) 4 else 8 // 75% chance 4€, else 8€
                val totalConDescuento = (totalOriginal - descuento).coerceAtLeast(0) // Avoid negative totals
                return Pair(totalConDescuento, descuento)
            }

            return Pair(totalOriginal, 0) // No discount applied
        }

        // Button click: confirm purchase
        btnRealizarCompra.setOnClickListener {
            // Calculate total price before discount
            val totalSinDescuento = listaCesta.sumOf { it.juego.precio * it.cesta.cantidad }
            val (totalConDescuento, descuentoAplicado) = calcularDescuento(totalSinDescuento)

            if (totalConDescuento <= 0) {
                Toast.makeText(this, "No hay nada en el carrito.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create an EditText for password input inside the dialog
            val input = EditText(this).apply {
                hint = "Introduce tu contraseña"
                inputType = android.text.InputType.TYPE_CLASS_TEXT or android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD
                typeface = Typeface.DEFAULT
                textSize = 16f
                setPadding(30, 20, 30, 20)
                maxWidth = 380
            }

            // Container for padding around EditText
            val container = FrameLayout(this).apply {
                setPadding(50, 40, 50, 10)
                addView(input)
            }

            // Build the confirmation dialog
            AlertDialog.Builder(this)
                .setTitle("Confirmar compra")
                .setMessage("Introduce tu contraseña para confirmar la compra")
                .setView(container)
                .setPositiveButton("Confirmar") { dialog, _ ->
                    val passwIngresada = input.text.toString()
                    if (passwIngresada.isBlank()) {
                        // Show message if password field is empty
                        Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show()
                        return@setPositiveButton
                    }

                    // Launch coroutine to validate password and process purchase asynchronously
                    lifecycleScope.launch {
                        val db = Aplicacion.getDatabase(applicationContext)
                        val userDao = db.userDao()
                        val usuario = userDao.obtenerUsuarioPorId(userId)

                        if (usuario != null && usuario.contrasena == passwIngresada) {
                            // Password correct, proceed with purchase
                            val pedidoDao = db.pedidoDao()
                            val detalleDao = db.detallePedidoDao()

                            val pedido = PedidoEntity(
                                usuarioId = userId,
                                fecha = System.currentTimeMillis(),
                                total = totalConDescuento // Total after discount applied
                            )

                            // Insert the order and get generated ID
                            val pedidoId = pedidoDao.insertar(pedido).toInt()

                            // Create order detail records for each cart item
                            val detalles = listaCesta.map {
                                DetallePedidoEntity(
                                    pedidoId = pedidoId,
                                    vjuegoId = it.juego.id,
                                    cantidad = it.cesta.cantidad,
                                    precioUnitario = it.juego.precio
                                )
                            }

                            detalleDao.insertar(*detalles.toTypedArray())

                            // Clear user's cart after purchase
                            db.cestaDao().eliminarTodoPorUsuario(userId)
                            listaCesta.clear()

                                withContext(Dispatchers.Main) {
                                    carritoAdapter.notifyDataSetChanged()
                                    actualizarTotal()
                                    val mensaje = if (descuentoAplicado > 0) {
                                        "¡Enhorabuena! Se ha descontado $descuentoAplicado€ a tu compra"
                                    } else {
                                        "Compra realizada correctamente"
                                    }

                                    // Show confirmation dialog
                                    AlertDialog.Builder(this@CarritoActivity)
                                        .setTitle("Compra realizada")
                                        .setMessage(mensaje)
                                        .setPositiveButton("OK") { dialog, _ -> dialog.dismiss() }
                                        .show()
                                }
                        } else {
                            withContext(Dispatchers.Main) {
                                // Wrong password message
                                Toast.makeText(this@CarritoActivity, "Contraseña incorrecta, compra no realizada", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()  // Close dialog on cancel
                }
                .show()
        }

        // Load cart data from database
        cargarDatos()
    }

    // Loads the current user's cart items with their associated games
    private fun cargarDatos() {
        val db = Aplicacion.getDatabase(applicationContext)
        val cestaDao = db.cestaDao()
        val juegoDao = db.vjuegoDao()

        lifecycleScope.launch {
            val cestaItems = cestaDao.obtenerPorUsuario(userId)
            val itemsConJuego = cestaItems.mapNotNull { cesta ->
                val juego = juegoDao.obtenerJuegoPorId(cesta.vjuegoId)
                if (juego != null && juego.disponible) {
                    CestaConVjuego(cesta, juego)
                } else {
                    null
                }
            }
            listaCesta.clear()
            listaCesta.addAll(itemsConJuego)
            carritoAdapter.notifyDataSetChanged()
            actualizarTotal()
        }
    }

    // Updates the total price TextView with the sum of all items in the cart
    private fun actualizarTotal() {
        val total = listaCesta.sumOf { it.juego.precio * it.cesta.cantidad }
        tvTotal.text = "Total: ${total}€"
    }

    // Saves quantity changes to the database
    private fun guardarCambiosEnDB(item: CestaConVjuego) {
        val cestaDao = Aplicacion.getDatabase(applicationContext).cestaDao()
        lifecycleScope.launch {
            cestaDao.actualizar(item.cesta)
        }
    }

    // Removes an item from the cart and database
    private fun eliminarDeCarrito(item: CestaConVjuego) {
        val cestaDao = Aplicacion.getDatabase(applicationContext).cestaDao()
        lifecycleScope.launch {
            cestaDao.eliminar(item.cesta)
            withContext(Dispatchers.Main) {
                listaCesta.remove(item)
                carritoAdapter.notifyDataSetChanged()
                actualizarTotal()
            }
        }
    }
}