package com.kitchenspal.model

import android.media.Image

data class UpdateAlamatUser(
    val alamat: String,
    val latitude: String,
    val longitude: String,
    val detail: String
)
data class UpdateImage(
    val image: Image
)
data class UpdateNama(
    val nama: String
)
data class UpdatePassword(
    val password: String
)
