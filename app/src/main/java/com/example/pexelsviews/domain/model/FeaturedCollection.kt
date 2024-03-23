package com.example.pexelsviews.domain.model

data class FeaturedCollection(
    val id: String,
    val title: String,
    val description: String,
    val isPrivate: Boolean,
    val mediaCount: Int,
    val photosCount: Int,
    val videosCount: Int
)
