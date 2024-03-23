package com.example.pexelsviews.domain.repository

import com.example.pexelsviews.domain.model.FeaturedCollection

interface CollectionRepository {
    suspend fun getFeaturedCollections(pageSize: Int): List<FeaturedCollection>
}