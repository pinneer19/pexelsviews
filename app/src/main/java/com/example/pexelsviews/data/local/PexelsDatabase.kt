package com.example.pexelsviews.data.local


import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.pexelsviews.data.local.dao.BookmarkDao
import com.example.pexelsviews.data.local.dao.PhotoDao
import com.example.pexelsviews.data.local.model.BookmarkEntity

import com.example.pexelsviews.data.local.model.PhotoEntity

@Database(
    entities = [PhotoEntity::class, BookmarkEntity::class],
    version = 1,
    exportSchema = false
)
abstract class PexelsDatabase: RoomDatabase() {
    abstract val photoDao: PhotoDao
    abstract val bookmarkDao: BookmarkDao
    companion object {
        const val DATABASE_NAME = "pexels_db"
    }
}