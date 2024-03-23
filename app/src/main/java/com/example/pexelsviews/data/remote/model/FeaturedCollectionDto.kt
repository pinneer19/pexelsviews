package com.example.pexelsviews.data.remote.model

data class FeaturedCollectionDto(
    val id: String,
    val title: String,
    val description: String,
    val isPrivate: Boolean,
    val mediaCount: Int,
    val photosCount: Int,
    val videosCount: Int
)