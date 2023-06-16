package com.kitchenspal.response

import com.kitchenspal.model.UserDataModel

data class UserResponse (
    val data: UserDataModel,
    val success: Boolean,
    val message: String
    )
