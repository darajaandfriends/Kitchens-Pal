package com.kitchenspal.model

import com.google.gson.annotations.SerializedName

data class UserDataModel(
    val id: Int,
    val nama: String,
    val email: String,
    val password: String,
    val alamat: String,
    val latitude: String,
    val longitude: String,
    val resetPasswordToken: String?,
    val token: String?,
    val detail: String,
    val image: String,
    val createdAt: String,
    val updatedAt: String
)