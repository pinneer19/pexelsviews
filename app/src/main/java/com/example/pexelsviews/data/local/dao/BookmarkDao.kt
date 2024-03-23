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
    suspend fun deleteBookmark(id: Int)

    @Query("SELECT EXISTS (SELECT 1 FROM bookmarkentity WHERE id = :id LIMIT 1)")
    suspend fun checkBookmarkStatus(id: Int): Boolean
}