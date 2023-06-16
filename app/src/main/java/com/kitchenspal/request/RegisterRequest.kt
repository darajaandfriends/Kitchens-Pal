package com.kitchenspal.request


data class RegisterRequest(
    val nama: String,
    val email: String,
    val password: String,
    val latitude: String,
    val longitude: String,
    val alamat: String,
    val detail: String
)
