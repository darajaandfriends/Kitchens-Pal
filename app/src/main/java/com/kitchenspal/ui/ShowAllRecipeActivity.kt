package com.kitchenspal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.ui.adapter.AllIngredientAdapter
import com.kitchenspal.ui.adapter.AllRecipeAdapter

class ShowAllRecipeActivity : AppCompatActivity() {

    private lateinit var editTextSearch: EditText
    private lateinit var recycleAllRecipe: RecyclerView
    private lateinit var allRecipeAdapter: AllRecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_show_all_recipe)
        editTextSearch = findViewById(R.id.editTextSearch)
        recycleAllRecipe = findViewById(R.id.recycleAllRecipe)

        initRecycle()
        getRecipe()

    }

    private fun initRecycle(){
        val layoutManager = GridLayoutManager(this, 2)
        allRecipeAdapter = AllRecipeAdapter()
        recycleAllRecipe.layoutManager = layoutManager
        recycleAllRecipe.adapter = allRecipeAdapter
    }
    private fun getRecipe() {
        val apiService = ApiService
        apiService.recipe(
            onSuccess = { recipeList ->
                allRecipeAdapter.setData(recipeList)
            }, { error ->
                Log.e("GetRecipes", "Error: ${error.length}")
            })
    }

}