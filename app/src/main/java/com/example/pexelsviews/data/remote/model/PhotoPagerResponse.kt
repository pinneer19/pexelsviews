package com.example.pexelsviews.data.remote.model

import com.google.gson.annotations.SerializedName

data class PhotoPagerResponse(
    val photos: List<PhotoDto>,
    val page: Int,
    @SerializedName("per_page") val perPage: Int,
    @SerializedName("total_results") val totalResults: Int,
    @SerializedName("prev_page") val prevPage: String?,
    @SerializedName("next_page") val nextPage: String?,
)