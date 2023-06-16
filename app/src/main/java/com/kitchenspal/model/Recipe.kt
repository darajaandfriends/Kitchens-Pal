package com.kitchenspal.model

data class Recipe(
    val id: Int,
    val recipeNama: String,
    val recipeIngredient: String,
    val recipeImage: String,
    val recipeDeskripsi: String,
    val recipeHarga: Int
)