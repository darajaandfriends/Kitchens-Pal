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
import com.kitchenspal.model.Recipe
import com.kitchenspal.ui.RecipeDetailsActivity

class RecipeAdapter : RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder>() {
    private val recipeList: MutableList<Recipe> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_recipe, parent, false)
        return RecipeViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        val currentRecipe = recipeList[position]
        holder.bind(currentRecipe)

    }

    override fun getItemCount(): Int {
        return recipeList.size
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setData(data: List<Recipe>) {
        recipeList.clear()
        recipeList.addAll(data)
        notifyDataSetChanged()
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeTitle: TextView = itemView.findViewById(R.id.recipeTitle)
        private val recipeDescription: TextView = itemView.findViewById(R.id.recipeDescription)
        private val recipeImage: ImageView = itemView.findViewById(R.id.recipeImage)

        fun bind(recipe: Recipe) {
            recipeTitle.text = recipe.recipeNama
            recipeDescription.text = recipe.recipeDeskripsi
            Glide.with(itemView)
                .load(recipe.recipeImage)
                .into(recipeImage)

            itemView.setOnClickListener {
                val intent = Intent(itemView.context, RecipeDetailsActivity::class.java)
                intent.putExtra("id", recipe.id)
                intent.putExtra("title", recipe.recipeNama)
                intent.putExtra("image", recipe.recipeImage)
                intent.putExtra("deskripsi", recipe.recipeDeskripsi)
                intent.putExtra("ingredient", recipe.recipeIngredient)
                intent.putExtra("harga", recipe.recipeHarga)
                itemView.context.startActivity(intent)
            }

        }
    }
}

