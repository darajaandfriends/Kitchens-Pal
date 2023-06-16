package com.kitchenspal.model

import com.google.gson.annotations.SerializedName

data class AddCartTransaction(
    @SerializedName("id_user")
    val userId: Int,
    val selectedCarts: Map<String, Int>
)
