package com.kitchenspal.response

import com.google.gson.annotations.SerializedName
import com.kitchenspal.model.HistoryItem

data class HistoryResponse(
    @SerializedName("rows") val data: List<HistoryItem>,
    val idUser: Int
    )
