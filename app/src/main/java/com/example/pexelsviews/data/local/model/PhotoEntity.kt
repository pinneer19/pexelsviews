package com.example.pexelsviews.data.local.model

import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.pexelsviews.data.model.PhotoSrcDto

@Entity
data class PhotoEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo("photo_id") val photoId: Int,
    @ColumnInfo("width") val width: Int,
    @ColumnInfo("height") val height: Int,
    @ColumnInfo("photographer") val photographer: String,
    @ColumnInfo("photographerUrl") val photographerUrl: String,
    @ColumnInfo("avgColor") val avgColor: String,
    @Embedded val src: PhotoSrcDto,
    @ColumnInfo("alt") val alt: String
)