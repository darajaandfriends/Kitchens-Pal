package com.kitchenspal.response

import com.kitchenspal.model.User

data class RegisterResponse(
    val success: Boolean,
    val message: String,
    val data: User?
)
