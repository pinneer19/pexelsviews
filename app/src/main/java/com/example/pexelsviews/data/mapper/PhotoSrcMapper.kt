package com.example.pexelsviews.data.mapper

import com.example.pexelsviews.data.model.PhotoSrcDto
import com.example.pexelsviews.domain.model.PhotoSrc

fun PhotoSrcDto.toPhotoSrc(): PhotoSrc {
    return PhotoSrc(
        original = original,
        large = large,
        large2x = large2x,
        small = small,
        medium = medium,
        portrait = portrait,
        landscape = landscape,
        tiny = tiny
    )
}

fun PhotoSrc.toPhotoSrcDto(): PhotoSrcDto {
    return PhotoSrcDto(
        original = original,
        large = large,
        large2x = large2x,
        small = small,
        medium = medium,
        portrait = portrait,
        landscape = landscape,
        tiny = tiny
    )
}