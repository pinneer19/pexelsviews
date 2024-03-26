package com.example.pexelsviews.domain.repository

import androidx.paging.PagingData
import com.example.pexelsviews.domain.model.Photo
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getBookmarks(): Flow<PagingData<Photo>>
    suspend fun addPhotoToBookmarks(photo: Photo)
    suspend fun deleteBookmark(id: Int, isBookmark: Boolean)
    suspend fun checkBookmarkStatus(id: Int, isBookmark: Boolean): Boolean
}