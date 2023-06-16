package com.kitchenspal.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.SearchView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kitchenspal.R
import com.kitchenspal.api.ApiService
import com.kitchenspal.ui.adapter.AllIngredientAdapter

class MoreIngredientsActivity : AppCompatActivity() {

    private lateinit var editTextSearch: SearchView
    private lateinit var recycleAllIngredient: RecyclerView
    private lateinit var allIngredientsAdapter: AllIngredientAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_more_ingredients)

        editTextSearch = findViewById(R.id.editTextSearch)
        recycleAllIngredient = findViewById(R.id.recycleAllIngredient)


        initRecycle()
        getIngredient()

    }

    private fun initRecycle() {
        val layoutManager = GridLayoutManager(this, 3)
        allIngredientsAdapter = AllIngredientAdapter()
        recycleAllIngredient.layoutManager = layoutManager
        recycleAllIngredient.adapter = allIngredientsAdapter
    }

    private fun getIngredient() {
        val apiService = ApiService
        apiService.ingredients(
            onSuccess = { ingredientList ->
                allIngredientsAdapter.setData(ingredientList)
            }, { error ->
                Log.e("GetIngredients", "Error: ${error.length}")
            })
    }
}
