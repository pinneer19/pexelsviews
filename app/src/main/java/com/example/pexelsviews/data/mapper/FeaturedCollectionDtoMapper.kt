package com.example.pexelsviews.data.mapper

import com.example.pexelsviews.data.remote.model.FeaturedCollectionDto
import com.example.pexelsviews.domain.model.FeaturedCollection

fun FeaturedCollectionDto.toFeaturedCollection(): FeaturedCollection {
    return FeaturedCollection(
        id = this.id,
        title = this.title,
        description = this.description,
        isPrivate = this.isPrivate,
        mediaCount = this.mediaCount,
        photosCount = this.photosCount,
        videosCount = this.videosCount
    )
}