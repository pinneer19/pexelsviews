package com.example.pexelsviews.data.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.pexelsviews.data.local.dao.BookmarkDao
import com.example.pexelsviews.data.mapper.toBookmarkEntity
import com.example.pexelsviews.data.mapper.toPhoto
import com.example.pexelsviews.domain.model.Photo
import com.example.pexelsviews.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val dao: BookmarkDao
) : BookmarkRepository {
    override fun getBookmarks(): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = PAGE_SIZE),
            pagingSourceFactory = {
                dao.getBookmarks()
            }
        )
            .flow
            .map { pagingData ->
                pagingData.map { entity -> entity.toPhoto() }
            }
    }

    override suspend fun addPhotoToBookmarks(photo: Photo) {
        dao.insertBookmark(photo.toBookmarkEntity())
    }

    override suspend fun deleteBookmark(id: Int, isBookmark: Boolean) {
        return if (isBookmark) {
            dao.deleteBookmarkById(id)
        } else {
            dao.deleteBookmarkByPhotoId(id)
        }
    }

    override suspend fun checkBookmarkStatus(id: Int, isBookmark: Boolean): Boolean {
        return if (isBookmark) {
            dao.checkBookmarkStatusById(id)
        } else {
            dao.checkBookmarkStatusByPhotoId(id)
        }
    }

    companion object {
        private const val PAGE_SIZE = 30
    }
}