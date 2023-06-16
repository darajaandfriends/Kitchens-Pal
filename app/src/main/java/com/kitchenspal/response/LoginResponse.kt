package com.kitchenspal.response

import com.google.gson.annotations.SerializedName


data class LoginResponse(
    val token: String,
    val message: String,
    @SerializedName("id_user") val idUser: Int
)