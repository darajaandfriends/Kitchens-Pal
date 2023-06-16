package com.kitchenspal.response

import com.google.gson.annotations.SerializedName
import com.kitchenspal.model.Product

data class CartResponse (
    @SerializedName("rows") val data: List<Product>,
    val idUser: Int
)