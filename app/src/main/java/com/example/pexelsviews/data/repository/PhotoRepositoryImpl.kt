package com.example.pexelsviews.data.repository

import android.content.Context
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.example.pexelsviews.data.local.PexelsDatabase
import com.example.pexelsviews.data.mapper.toPhoto
import com.example.pexelsviews.data.remote.api.PexelsApiService
import com.example.pexelsviews.data.remote.mediator.PhotoRemoteMediator
import com.example.pexelsviews.data.utils.FileDownloader
import com.example.pexelsviews.domain.model.Photo
import com.example.pexelsviews.domain.repository.PhotoRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class PhotoRepositoryImpl @Inject constructor(
    private val apiService: PexelsApiService,
    private val db: PexelsDatabase,
    private val context: Context
) : PhotoRepository {

    override suspend fun getPhotoDetails(id: Int, fromDatabase: Boolean): Photo {
        return if (fromDatabase) {
            db.bookmarkDao.getBookmark(id).toPhoto()
        } else {
            apiService.getPhotoById(id).toPhoto()
        }
    }

    override fun downloadPhoto(photoUrl: String, authToken: String): Long {
        val downloader = FileDownloader(context = context, token = authToken)
        return downloader.download(url = photoUrl)
    }

    @OptIn(ExperimentalPagingApi::class)
    override suspend fun getPhotos(query: String, pageSize: Int): Flow<PagingData<Photo>> {
        return Pager(
            config = PagingConfig(pageSize = pageSize),
            remoteMediator = PhotoRemoteMediator(
                photoDb = db,
                apiService = apiService
            ),
            pagingSourceFactory = {
                db.photoDao.getPhotos(query = query)
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toPhoto() }
        }
    }
}