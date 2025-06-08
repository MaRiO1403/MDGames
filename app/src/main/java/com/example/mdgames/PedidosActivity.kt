package com.example.mdgames

import PedidoAdapter
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mdgames.ManejoDeSesiones.Sesiones
import com.example.mdgames.entity.PedidoEntity
import kotlinx.coroutines.launch

class PedidosActivity : AppCompatActivity() {

    private lateinit var recyclerPedidos: RecyclerView
    private lateinit var adapter: PedidoAdapter
    private var listaPedidos: MutableList<PedidoEntity> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_pedidos)

        // Initialize the RecyclerView
        recyclerPedidos = findViewById(R.id.recyclerPedidos)

        // Set up the adapter with a click listener for each order
        adapter = PedidoAdapter(listaPedidos) { pedido ->
            mostrarDetallesPedido(pedido.id) // Show order details when clicked
        }
        recyclerPedidos.layoutManager = LinearLayoutManager(this)
        recyclerPedidos.adapter = adapter

        // Get the logged-in user ID and load their orders
        val userId = Sesiones.obtenerSesion(applicationContext) ?: return
        cargarPedidos(userId)
    }

    // Loads all orders for a given user from the database
    private fun cargarPedidos(userId: Int) {
        val pedidoDao = Aplicacion.getDatabase(applicationContext).pedidoDao()

        lifecycleScope.launch {
            val pedidos = pedidoDao.obtenerPedidosPorUsuario(userId)
            // Clear current list and add the new ones in descending date order
            listaPedidos.clear()
            listaPedidos.addAll(pedidos.sortedByDescending { it.fecha })
            adapter.notifyDataSetChanged() // Notify adapter to refresh the list
        }
    }

    // Shows a dialog with the details of a selected order
    private fun mostrarDetallesPedido(pedidoId: Int) {
        val detalleDao = Aplicacion.getDatabase(this).detallePedidoDao()

        lifecycleScope.launch {
            // Get the order details joined with game info
            val detalles = detalleDao.obtenerDetalleConVjuego(pedidoId)

            // Format the details into a readable message
            val mensaje = detalles.joinToString("\n") { d ->
                "- ${d.juego.titulo} (x${d.detalle.cantidad}) = ${d.detalle.precioUnitario * d.detalle.cantidad}€\n" + " " + "[${d.juego.plataforma}]\n"
            }

            // Display the message in an alert dialog
            runOnUiThread {
                AlertDialog.Builder(this@PedidosActivity)
                    .setTitle("Detalles del pedido")
                    .setMessage(mensaje)
                    .setPositiveButton("Cerrar", null)
                    .show()
            }
        }
    }
}