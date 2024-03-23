package com.example.pexelsviews.domain.model

data class Photo(
    val id: Int,
    val width: Int,
    val height: Int,
    val photographer: String,
    val photographerUrl: String,
    val avgColor: String,
    val src: PhotoSrc,
    val alt: String
)
