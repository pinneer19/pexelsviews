package com.example.pexelsviews.data.remote.mediator

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.example.pexelsviews.data.local.PexelsDatabase
import com.example.pexelsviews.data.local.model.PhotoEntity
import com.example.pexelsviews.data.mapper.toPhotoEntity
import com.example.pexelsviews.data.remote.api.PexelsApiService
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class PhotoRemoteMediator @Inject constructor(
    private val photoDb: PexelsDatabase,
    private val apiService: PexelsApiService
) : RemoteMediator<Int, PhotoEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, PhotoEntity>
    ): MediatorResult {
        return try {
            val page = when (loadType) {
                LoadType.REFRESH -> DEFAULT_PAGE
                LoadType.PREPEND -> return MediatorResult.Success(endOfPaginationReached = true)
                LoadType.APPEND -> {
                    val lastItem = state.lastItemOrNull()
                    lastItem?.let { it.id / state.config.pageSize + 1 } ?: (DEFAULT_PAGE + 1)
                }
            }
            val response = apiService.getPhotosByPage(
                page = page,
                pageSize = state.config.pageSize
            )
            photoDb.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    photoDb.photoDao.clearPhotos()
                }
                val photoEntities = response.photos.map { photo -> photo.toPhotoEntity() }
                photoDb.photoDao.insertAll(photoEntities)
            }
            MediatorResult.Success(endOfPaginationReached = response.photos.isEmpty())
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    companion object {
        private const val DEFAULT_PAGE = 1
    }
}