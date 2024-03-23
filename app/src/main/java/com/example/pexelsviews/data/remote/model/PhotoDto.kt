package com.example.pexelsviews.data.remote.model

import com.example.pexelsviews.data.model.PhotoSrcDto
import com.google.gson.annotations.SerializedName

data class PhotoDto(
    val id: Int,
    val width: Int,
    val height: Int,
    val photographer: String,
    @SerializedName("photographer_url") val photographerUrl: String,
    @SerializedName("avg_color") val avgColor: String,
    val src: PhotoSrcDto,
    val alt: String
)
