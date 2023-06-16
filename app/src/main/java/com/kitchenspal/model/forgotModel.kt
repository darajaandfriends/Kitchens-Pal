package com.kitchenspal.model

data class ResetPasswordRequest(
    val email: String
)

data class ResetPasswordResponse(
    val message: String,
    val token: String
)

data class UpdatePasswordRequest(
    val password: String
)

data class UpdatePasswordResponse(
    val message: String
)
