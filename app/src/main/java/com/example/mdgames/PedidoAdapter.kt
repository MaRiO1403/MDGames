import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mdgames.R
import com.example.mdgames.entity.PedidoEntity
import java.text.SimpleDateFormat
import java.util.*

class PedidoAdapter(
    private val pedidos: List<PedidoEntity>, // List of orders to display
    private val onPedidoClick: (PedidoEntity) -> Unit // Callback when an order is clicked
) : RecyclerView.Adapter<PedidoAdapter.PedidoViewHolder>() {

    // Formatter for displaying the order date
    private val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    // ViewHolder class that holds the views for each item
    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvPedidoId: TextView = itemView.findViewById(R.id.tvPedidoId)
        val tvFechaPedido: TextView = itemView.findViewById(R.id.tvFechaPedido)
        val tvTotalPedido: TextView = itemView.findViewById(R.id.tvTotalPedido)
    }

    // Inflates the layout for each item in the RecyclerView
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_pedido, parent, false)
        return PedidoViewHolder(view)
    }

    // Binds the data to the views for a given position
    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]

        holder.tvPedidoId.text = "Pedido"
        holder.tvFechaPedido.text = "Fecha: ${dateFormat.format(Date(pedido.fecha))}"
        holder.tvTotalPedido.text = "Total: ${pedido.total}€"

        // Set click listener to trigger the callback with the selected order
        holder.itemView.setOnClickListener {
            onPedidoClick(pedido)
        }
    }

    // Returns the total number of items in the list
    override fun getItemCount(): Int = pedidos.size
}
