package com.example.pexelsviews.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Upsert
import com.example.pexelsviews.data.local.model.PhotoEntity

@Dao
interface PhotoDao {
    @Query("SELECT * FROM photoentity WHERE id = :id")
    suspend fun getPhotoDetails(id: Int): PhotoEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addPhotoToBookmarks(photo: PhotoEntity)

    @Upsert(entity = PhotoEntity::class)
    suspend fun insertAll(photos: List<PhotoEntity>)

    @Query("SELECT * FROM photoentity WHERE alt LIKE '%' || :query || '%'")
    fun getPhotos(query: String): PagingSource<Int, PhotoEntity>

    @Query("DELETE FROM photoentity")
    suspend fun clearPhotos()
}