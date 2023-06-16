package com.kitchenspal.model

data class CheckoutItem(
    val cart_nama: String,
    val cart_image: String,
    val cart_harga: Int,
    val cart_qty: Int,
    val id_user: Int,
)