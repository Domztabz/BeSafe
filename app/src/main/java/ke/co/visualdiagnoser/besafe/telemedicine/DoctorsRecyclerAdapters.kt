package ke.co.visualdiagnoser.besafe.telemedicine

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ke.co.visualdiagnoser.besafe.R

class CartImagesAdapter(val items : List<String>, val context: Context) : RecyclerView.Adapter<ViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.item_product_image, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(items.get(position)).into(holder.product_image)
    }
}

class ViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image = view.product_image
}

class CartItemsAdapter(val items : List<Cart>, val context: Context) : RecyclerView.Adapter<CartItemsAdapterViewHolder>() {

    // Gets the number of animals in the list
    override fun getItemCount(): Int {
        return items.size
    }

    // Inflates the item views
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemsAdapterViewHolder {
        return CartItemsAdapterViewHolder(LayoutInflater.from(context).inflate(R.layout.item_checkout_cart_item, parent, false))
    }

    // Binds each animal in the ArrayList to a view
    override fun onBindViewHolder(holder: CartItemsAdapterViewHolder, position: Int) {
        holder.bindItem(items.get(position))
    }
}

class CartItemsAdapterViewHolder (view: View) : RecyclerView.ViewHolder(view) {
    // Holds the TextView that will add each animal to
    val product_image = view.product_image
    fun bindItem(cartItem: Cart) {
        val price_ = cartItem.quantity * cartItem.price
        val formatter = DecimalFormat("#,###,###");
        val priceFormattedString = formatter.format(price_.toInt());

        itemView.price_tv.text = "KES $priceFormattedString"
        itemView.description_product.text = cartItem.name
        itemView.quantity_tv.text = cartItem.quantity.toString()



    }

}