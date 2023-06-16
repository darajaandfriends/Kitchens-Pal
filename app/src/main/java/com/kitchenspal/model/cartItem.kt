package com.kitchenspal.model

import com.google.gson.annotations.SerializedName

data class cartItem(
    @SerializedName("cart_nama")
    val title: String,
    @SerializedName("cart_image")
    val image: String,
    @SerializedName("cart_harga")
    val harga: Int,
    @SerializedName("cart_qty")
    val quantity: Int,
    @SerializedName("id_user")
    val id_user: Int
)

