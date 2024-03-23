package com.example.pexelsviews.data.mapper

import com.example.pexelsviews.data.local.model.PhotoEntity
import com.example.pexelsviews.data.remote.model.PhotoDto
import com.example.pexelsviews.domain.model.Photo

fun PhotoDto.toPhotoEntity(): PhotoEntity {
    return PhotoEntity(
        width = this.width,
        height = this.height,
        photographer = this.photographer,
        photographerUrl = this.photographerUrl,
        avgColor = this.avgColor,
        src = this.src,
        alt = this.alt,
        photoId = id
    )
}

fun PhotoDto.toPhoto(): Photo {
    return Photo(
        id = id,
        width = width,
        height = height,
        photographer = photographer,
        photographerUrl = photographerUrl,
        avgColor = avgColor,
        alt = alt,
        src = src.toPhotoSrc()
    )
}