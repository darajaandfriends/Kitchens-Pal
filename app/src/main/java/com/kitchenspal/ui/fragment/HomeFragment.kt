package com.kitchenspal.ui.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.model.Ingredients
import com.kitchenspal.model.Recipe
import com.kitchenspal.ui.CartActivity
import com.kitchenspal.ui.MoreIngredientsActivity
import com.kitchenspal.ui.SearchAllActivity
import com.kitchenspal.ui.ShowAllRecipeActivity
import com.kitchenspal.ui.adapter.IngredientsAdapter
import com.kitchenspal.ui.adapter.RecipeAdapter


class HomeFragment : Fragment() {
    private lateinit var ingredientsAdapter: IngredientsAdapter
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var recyclerViewIngredients: RecyclerView
    private lateinit var recyclerViewRecipe: RecyclerView
    private lateinit var apiService: ApiService
    private lateinit var btcart :ImageView
    private lateinit var btMoreIngredients: TextView
    private lateinit var btShowAllRecipe: TextView
    private lateinit var btSearch: EditText

    override fun onAttach(context: Context) {
        super.onAttach(context)
        apiService = ApiService
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerViewIngredients = view.findViewById(R.id.recyclerViewIngredients)
        recyclerViewRecipe = view.findViewById(R.id.recyclerViewRecipe)
        btMoreIngredients = view.findViewById(R.id.btMoreIngredient)
        btShowAllRecipe = view.findViewById(R.id.btshowAllRecipe)
        btcart = view.findViewById(R.id.cart)
        btSearch = view.findViewById(R.id.search)

        btSearch.setOnClickListener {
            val intent = Intent(requireContext(), SearchAllActivity::class.java)
            startActivity(intent)
        }

        btcart.setOnClickListener {
            val intent = Intent(requireContext(), CartActivity::class.java)
            startActivity(intent)
        }

        initRecyclerViews()
        getIngredients()
        getRecipes()
        moreIngredients()
        showAllRecipe()
    }

    private fun initRecyclerViews() {
        ingredientsAdapter = IngredientsAdapter()
        recyclerViewIngredients.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recyclerViewIngredients.adapter = ingredientsAdapter

        recipeAdapter = RecipeAdapter()
        recyclerViewRecipe.layoutManager = LinearLayoutManager(requireContext())
        recyclerViewRecipe.adapter = recipeAdapter
    }

    private fun getIngredients() {
        apiService.ingredients(
            onSuccess = { ingredientList ->
                ingredientsAdapter.setData(ingredientList)
            }, { error ->
            Log.e("GetIngredients", "Error: ${error.length}")
        })
    }

    private fun getRecipes() {
        apiService.recipe(
            onSuccess = { recipeList ->
            recipeAdapter.setData(recipeList)
        }, { error ->
                println("Error: $error")
            Log.e("GetRecipes", "Error: ${error.length}")
        })
    }
    private fun moreIngredients() {
        btMoreIngredients.setOnClickListener {
            val intent = Intent(requireContext(), MoreIngredientsActivity::class.java)
            startActivity(intent)
        }
    }

    private fun showAllRecipe() {
        btShowAllRecipe.setOnClickListener {
            val intent = Intent(requireContext(), ShowAllRecipeActivity::class.java)
            startActivity(intent)
        }
    }
}
