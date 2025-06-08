import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mdgames.R
import com.example.mdgames.entity.VJuegoEntity

class VJuegoAdapter(
    private val juegos: MutableList<VJuegoEntity>,
    private val clickAddCarrito: (VJuegoEntity) -> Unit = {}, // Callback when add to cart button is clicked
    private val isModifyActivity: Boolean, // Controls whether the cart button is visible
    private val onItemClick: ((VJuegoEntity) -> Unit)? = null
) : RecyclerView.Adapter<VJuegoAdapter.VJuegoViewHolder>() {

    // ViewHolder class that holds references to the views in each item
    inner class VJuegoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvImagen: ImageView = itemView.findViewById(R.id.tvImage)
        val tvTitulo: TextView = itemView.findViewById(R.id.tvTitle)
        val tvDescripcion: TextView = itemView.findViewById(R.id.tvDescription)
        val tvPrecio: TextView = itemView.findViewById(R.id.textView3)
        val botonCarrito: ImageButton = itemView.findViewById(R.id.cartButton)
    }

    // Inflates the item layout and returns a ViewHolder
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VJuegoViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_vjuego, parent, false)
        return VJuegoViewHolder(view)
    }

    // Binds the data of a video game to each item view
    override fun onBindViewHolder(holder: VJuegoViewHolder, position: Int) {
        val juego = juegos[position]
        holder.tvTitulo.text = juego.titulo
        holder.tvDescripcion.text = juego.plataforma
        holder.tvPrecio.text = "${juego.precio} €"

        // Load image using Glide
        Glide.with(holder.itemView.context)
            .load(juego.caratulaUrl)
            .placeholder(R.drawable.ic_launcher_background) // Default image while loading
            .into(holder.tvImagen)

        // Hide cart button if we are in a modify/admin screen
        if (isModifyActivity) {
            holder.botonCarrito.visibility = View.GONE
        } else {
            // Show cart button and set its action
            holder.botonCarrito.setOnClickListener {
                clickAddCarrito(juego)
            }
        }

        // Set the click listener for the entire item
        holder.itemView.setOnClickListener {
                onItemClick?.invoke(juego)
        }
    }

    // Returns total number of items
    override fun getItemCount(): Int = juegos.size

    // Method to update the adapter’s data and refresh the view
    fun actualizarLista(nuevosJuegos: List<VJuegoEntity>) {
        juegos.clear()
        juegos.addAll(nuevosJuegos)
        notifyDataSetChanged()
    }
}