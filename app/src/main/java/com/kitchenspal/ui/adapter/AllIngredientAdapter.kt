package com.kitchenspal.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kitchenspal.R
import com.kitchenspal.model.Ingredients
import com.kitchenspal.ui.RecipeDetailsActivity

class AllIngredientAdapter : RecyclerView.Adapter<AllIngredientAdapter.IngredientViewHolder>() {
    private val ingredientsList: MutableList<Ingredients> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IngredientViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_all, parent, false)
        return IngredientViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: IngredientViewHolder, position: Int) {
        val currentIngredient = ingredientsList[position]
        holder.bind(currentIngredient)
    }

    override fun getItemCount(): Int {
        return ingredientsList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Ingredients>) {
        ingredientsList.clear()
        ingredientsList.addAll(data)
        notifyDataSetChanged()
    }

    inner class IngredientViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val ingredientImage: ImageView = itemView.findViewById(R.id.allImage)
        private val ingredientTitle: TextView = itemView.findViewById(R.id.allTitle)

        fun bind(ingredient: Ingredients) {
            Glide.with(itemView)
                .load(ingredient.ingredientsImage)
                .into(ingredientImage)
            ingredientTitle.text = ingredient.ingredientsNama

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, RecipeDetailsActivity::class.java)
                intent.putExtra("id", ingredient.id)
                intent.putExtra("title", ingredient.ingredientsNama)
                intent.putExtra("image", ingredient.ingredientsImage)
                intent.putExtra("jenis", ingredient.ingredientsJenis)
                intent.putExtra("deskripsi", ingredient.ingredientsDeskripsi)
                intent.putExtra("harga", ingredient.ingredientsHarga)
                itemView.context.startActivity(intent)
            }
        }
    }
}


