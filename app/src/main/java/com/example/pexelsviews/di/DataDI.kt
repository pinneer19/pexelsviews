package com.example.pexelsviews.di

import android.app.Application
import android.content.Context
import androidx.room.Room
import com.example.pexelsviews.BuildConfig
import com.example.pexelsviews.data.local.PexelsDatabase
import com.example.pexelsviews.data.remote.api.PexelsApiService
import com.example.pexelsviews.data.repository.BookmarkRepositoryImpl
import com.example.pexelsviews.data.repository.CollectionRepositoryImpl
import com.example.pexelsviews.data.repository.PhotoRepositoryImpl
import com.example.pexelsviews.domain.repository.BookmarkRepository
import com.example.pexelsviews.domain.repository.CollectionRepository
import com.example.pexelsviews.domain.repository.PhotoRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    private const val REQUEST_HEADER_AUTH = "Authorization"

    @Provides
    @Singleton
    fun providePhotoDatabase(app: Application): PexelsDatabase {
        return Room.databaseBuilder(
            app,
            PexelsDatabase::class.java,
            PexelsDatabase.DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideApiService(): PexelsApiService {
        val retrofit = Retrofit.Builder()
            .baseUrl(BuildConfig.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(
                OkHttpClient.Builder().addInterceptor { chain ->
                    val request = chain.request()
                        .newBuilder()
                        .addHeader(REQUEST_HEADER_AUTH, BuildConfig.API_KEY)
                        .build()
                    chain.proceed(request)
                }.build()
            )
            .build()

        return retrofit.create(PexelsApiService::class.java)
    }

    @Provides
    @Singleton
    fun provideBookmarkRepository(photoDb: PexelsDatabase): BookmarkRepository {
        return BookmarkRepositoryImpl(dao = photoDb.bookmarkDao)
    }

    @Provides
    @Singleton
    fun providePhotoRepository(
        apiService: PexelsApiService,
        photoDb: PexelsDatabase,
        @ApplicationContext context: Context
    ): PhotoRepository {
        return PhotoRepositoryImpl(
            apiService = apiService,
            db = photoDb,
            context = context,
            authToken = BuildConfig.API_KEY
        )
    }

    @Provides
    @Singleton
    fun provideCollectionRepository(apiService: PexelsApiService): CollectionRepository {
        return CollectionRepositoryImpl(apiService = apiService)
    }
}