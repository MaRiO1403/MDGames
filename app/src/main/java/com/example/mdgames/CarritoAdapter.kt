import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mdgames.R
import com.example.mdgames.entity.CestaConVjuego
import com.example.mdgames.entity.CestaEntity

class CarritoAdapter(
    private val items: List<CestaConVjuego>,
    private val onIncrement: (CestaEntity) -> Unit, // Callback to increase quantity
    private val onDecrement: (CestaEntity) -> Unit, // Callback to decrease quantity
    private val onRemove: (CestaEntity) -> Unit // Callback to remove item
) : RecyclerView.Adapter<CarritoAdapter.CarritoViewHolder>() {

    // ViewHolder class holds references to each item view's UI components
    inner class CarritoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitulo)
        val tvPlataforma: TextView = itemView.findViewById(R.id.tvPlataforma)
        val tvPrecio: TextView = itemView.findViewById(R.id.tvPrecio)
        val tvCantidad: TextView = itemView.findViewById(R.id.tvCantidad)
        val imgCaratula: ImageView = itemView.findViewById(R.id.tvImage)
        val btnMas: Button = itemView.findViewById(R.id.btnMas)
        val btnMenos: Button = itemView.findViewById(R.id.btnMenos)
        val btnEliminar: Button = itemView.findViewById(R.id.btnEliminar)
    }

    // Inflate the item layout and create a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CarritoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vjcarrito, parent, false)
        return CarritoViewHolder(view)
    }

    // Bind data to the views in each ViewHolder
    override fun onBindViewHolder(holder: CarritoViewHolder, position: Int) {
        val (cesta, juego) = items[position]

        holder.tvTitulo.text = juego.titulo
        holder.tvPlataforma.text = juego.plataforma
        holder.tvPrecio.text = "${juego.precio}€"
        holder.tvCantidad.text = cesta.cantidad.toString()

        // Load game cover image asynchronously using Glide with a placeholder image
        Glide.with(holder.itemView.context)
            .load(juego.caratulaUrl)
            .placeholder(R.drawable.ic_launcher_background)
            .into(holder.imgCaratula)

        // Handle increase quantity button click
        holder.btnMas.setOnClickListener {
            onIncrement(cesta)
        }

        // Handle decrease quantity button click
        holder.btnMenos.setOnClickListener {
            onDecrement(cesta)
        }

        // Handle remove item button click
        holder.btnEliminar.setOnClickListener {
            onRemove(cesta)
        }
    }

    // Returns total number of items in the list
    override fun getItemCount(): Int = items.size
}
