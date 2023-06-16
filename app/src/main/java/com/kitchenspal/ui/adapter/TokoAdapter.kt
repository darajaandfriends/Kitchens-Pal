package com.kitchenspal.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.kitchenspal.R
import com.kitchenspal.model.TokoItem

class TokoAdapter(
    private var tokoList: List<TokoItem>,
    private val listener: OnItemSelectedListener
) : RecyclerView.Adapter<TokoAdapter.TokoViewHolder>() {

    private var selectedPosition = -1

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TokoViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_toko, parent, false)
        return TokoViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TokoViewHolder, @SuppressLint("RecyclerView") position: Int) {
        val currentToko = tokoList[position]
        holder.bind(currentToko, position)

        holder.radioButtonPilih.setOnClickListener {
            val previouslySelectedPosition = selectedPosition
            selectedPosition = position
            notifyItemChanged(previouslySelectedPosition)
            notifyItemChanged(selectedPosition)
            listener.onItemSelected(selectedPosition)
        }
    }

    override fun getItemCount(): Int {
        return tokoList.size
    }

    inner class TokoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tokoTextView: TextView = itemView.findViewById(R.id.tokoTextView)
        val radioButtonPilih: RadioButton = itemView.findViewById(R.id.radioButtonPilih)

        fun bind(tokoItem: TokoItem, position: Int) {
            tokoTextView.text = tokoItem.toko_nama
            radioButtonPilih.isChecked = position == selectedPosition
        }
    }

    fun updateList(newList: List<TokoItem>) {
        tokoList = newList
        notifyDataSetChanged()
    }

    interface OnItemSelectedListener {
        fun onItemSelected(position: Int)
    }
}
