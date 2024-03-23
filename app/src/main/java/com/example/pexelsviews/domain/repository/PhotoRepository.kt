package com.example.pexelsviews.domain.repository

import androidx.paging.PagingData
import com.example.pexelsviews.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface PhotoRepository {
    suspend fun getPhotoDetails(id: Int, fromDatabase: Boolean = false): Photo
    suspend fun getPhotos(query: String, pageSize: Int): Flow<PagingData<Photo>>
    fun downloadPhoto(photoUrl: String, authToken: String): Long
}