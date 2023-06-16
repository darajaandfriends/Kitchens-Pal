package com.kitchenspal.model

import com.google.gson.annotations.SerializedName

data class HistoryItem(
    val id: Int,
    @SerializedName("cart_nama")
    val title: String,
    @SerializedName("cart_image")
    val image: String,
    @SerializedName("cart_harga")
    val price: Int,
    @SerializedName("cart_qty")
    val quantity: Float,
)
