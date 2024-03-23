package com.example.pexelsviews.data.mapper

import com.example.pexelsviews.data.local.model.PhotoEntity
import com.example.pexelsviews.domain.model.Photo

fun PhotoEntity.toPhoto(): Photo {
    return Photo(
        id = this.photoId,
        width = this.width,
        height = this.height,
        photographer = this.photographer,
        photographerUrl = this.photographerUrl,
        avgColor = this.avgColor,
        src = this.src.toPhotoSrc(),
        alt = this.alt
    )
}