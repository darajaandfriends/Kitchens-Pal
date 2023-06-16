package com.kitchenspal.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.Product

class CartAdapter(private val context: Context) : RecyclerView.Adapter<CartAdapter.ProductViewHolder>() {

    private val productList: MutableList<Product> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_cart, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = productList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    fun removeItem(product: Product) {
        val position = productList.indexOf(product)
        if (position != -1) {
            productList.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    fun setData(products: List<Product>) {
        productList.clear()
        productList.addAll(products)
        notifyDataSetChanged()
    }

    fun getProductList(): List<Product> {
        return productList
    }

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        private val productCheckbox: CheckBox = itemView.findViewById(R.id.productCheckbox)

        fun bind(product: Product) {
            productName.text = product.title
            productPrice.text = product.price.toString()
            productQuantity.text = product.quantity.toString()
            productCheckbox.isChecked = product.isSelected

            Glide.with(itemView)
                .load(product.image)
                .into(productImage)

            productCheckbox.setOnCheckedChangeListener { _, isChecked ->
                product.isSelected = isChecked
            }
        }
    }
}
