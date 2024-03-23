package com.example.pexelsviews.data.repository

import com.example.pexelsviews.data.mapper.toFeaturedCollection
import com.example.pexelsviews.data.remote.api.PexelsApiService
import com.example.pexelsviews.domain.model.FeaturedCollection
import com.example.pexelsviews.domain.repository.CollectionRepository
import javax.inject.Inject

class CollectionRepositoryImpl @Inject constructor(
    private val apiService: PexelsApiService
) : CollectionRepository {
    override suspend fun getFeaturedCollections(pageSize: Int): List<FeaturedCollection> {
        return apiService.getFeaturedCollections(pageSize = pageSize)
            .collections
            .map { dtoCollection -> dtoCollection.toFeaturedCollection() }
    }
}