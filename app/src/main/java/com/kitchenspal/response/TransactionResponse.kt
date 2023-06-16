package com.kitchenspal.response

data class TransactionResponse(
    val transactionId: String,
    val status: String,
    val message: String
)
