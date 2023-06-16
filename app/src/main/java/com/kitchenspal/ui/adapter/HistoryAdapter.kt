package com.kitchenspal.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kitchenspal.R
import com.kitchenspal.model.HistoryItem
import com.kitchenspal.model.Product

class HistoryAdapter(private val context: Context) : RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder>() {

    private val historyList: MutableList<HistoryItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_history, parent, false)
        return HistoryViewHolder(view)
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val product = historyList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return historyList.size
    }


    fun setData(products: List<HistoryItem>) {
        historyList.clear()
        historyList.addAll(products)
        notifyDataSetChanged()
    }


    inner class HistoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)

        fun bind(product: HistoryItem) {
            productName.text = product.title
            productPrice.text = product.price.toString()
            productQuantity.text = product.quantity.toString()

            Glide.with(itemView)
                .load(product.image)
                .into(productImage)

            }
        }
    }
