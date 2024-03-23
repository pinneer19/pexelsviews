package com.example.pexelsviews.data.remote.api

import com.example.pexelsviews.data.remote.model.FeaturedCollectionsResponse
import com.example.pexelsviews.data.remote.model.PhotoDto
import com.example.pexelsviews.data.remote.model.PhotoPagerResponse
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PexelsApiService {
    @GET("collections/featured")
    suspend fun getFeaturedCollections(
        @Query("page") page: Int? = null,
        @Query("per_page") pageSize: Int? = null
    ): FeaturedCollectionsResponse

    @GET("curated")
    suspend fun getPhotosByPage(
        @Query("page") page: Int? = null,
        @Query("per_page") pageSize: Int? = null
    ): PhotoPagerResponse

    @GET("photos/{id}")
    suspend fun getPhotoById(@Path("id") id: Int): PhotoDto
}