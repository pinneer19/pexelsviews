package com.example.pexelsviews.data.mapper

import com.example.pexelsviews.data.local.model.BookmarkEntity
import com.example.pexelsviews.data.remote.model.PhotoDto
import com.example.pexelsviews.domain.model.Photo

fun Photo.toPhotoDto(): PhotoDto {
    return PhotoDto(
        id = id,
        width = width,
        height = height,
        photographer = photographer,
        photographerUrl = photographerUrl,
        avgColor = avgColor,
        alt = alt,
        src = src.toPhotoSrcDto()
    )
}

fun Photo.toBookmarkEntity(): BookmarkEntity {
    return BookmarkEntity(
        width = width,
        height = height,
        photographer = photographer,
        photographerUrl = photographerUrl,
        avgColor = avgColor,
        alt = alt,
        src = src.toPhotoSrcDto(),
        photoId = id
    )
}