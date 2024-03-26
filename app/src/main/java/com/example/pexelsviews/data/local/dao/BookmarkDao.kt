package com.example.pexelsviews.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.example.pexelsviews.data.local.model.BookmarkEntity

@Dao
interface BookmarkDao {

    @Query("SELECT * FROM bookmarkentity")
    fun getBookmarks(): PagingSource<Int, BookmarkEntity>

    @Query("SELECT * FROM bookmarkentity WHERE id = :id")
    suspend fun getBookmark(id: Int): BookmarkEntity

    @Upsert
    suspend fun insertBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarkentity WHERE id = :id")
    suspend fun deleteBookmarkById(id: Int)

    @Query("DELETE FROM bookmarkentity WHERE photo_id = :id")
    suspend fun deleteBookmarkByPhotoId(id: Int)

    @Query("SELECT EXISTS (SELECT 1 FROM bookmarkentity WHERE photo_id = :id LIMIT 1)")
    suspend fun checkBookmarkStatusByPhotoId(id: Int): Boolean

    @Query("SELECT EXISTS (SELECT 1 FROM bookmarkentity WHERE id = :id LIMIT 1)")
    suspend fun checkBookmarkStatusById(id: Int): Boolean
}