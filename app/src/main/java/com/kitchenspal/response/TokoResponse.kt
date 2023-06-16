package com.kitchenspal.response

import com.google.gson.annotations.SerializedName
import com.kitchenspal.model.TokoItem

data class TokoResponse(
    @SerializedName("rows")
    val tokoList: List<TokoItem>
)
