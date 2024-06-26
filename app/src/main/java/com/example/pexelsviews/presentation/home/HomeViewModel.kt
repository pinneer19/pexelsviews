package com.example.pexelsviews.presentation.home

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.example.pexelsviews.domain.model.FeaturedCollection
import com.example.pexelsviews.domain.model.Photo
import com.example.pexelsviews.domain.repository.CollectionRepository
import com.example.pexelsviews.domain.repository.PhotoRepository
import com.example.pexelsviews.presentation.utils.NetworkObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

@OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
@HiltViewModel
class HomeViewModel @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val collectionRepository: CollectionRepository,
    @ApplicationContext val context: Context,
) : ViewModel() {

    private val _homeCollectionState =
        MutableStateFlow<HomeCollectionState>(HomeCollectionState.Idle)
    val homeCollectionState = _homeCollectionState.asStateFlow()

    val pager: Flow<PagingData<Photo>>
    private val _searchText = MutableStateFlow(emptyString())

    val networkObserver: NetworkObserver

    init {
        loadCollections()
        pager = _searchText
            .debounce(timeoutMillis = 500L)
            .flatMapLatest {
                photoRepository.getPhotos(it, PHOTOS_PAGE_SIZE)
            }
            .cachedIn(viewModelScope)
        networkObserver = NetworkObserver(context)
    }

    fun loadCollections() {
        viewModelScope.launch {
            _homeCollectionState.emit(HomeCollectionState.Loading)
            try {
                val collectionsResponse =
                    collectionRepository.getFeaturedCollections(COLLECTIONS_PAGE_SIZE)
                _homeCollectionState.emit(HomeCollectionState.Success(collections = collectionsResponse))
            } catch (e: HttpException) {
                _homeCollectionState.emit(HomeCollectionState.Error(error = e.message.toString()))
            } catch (e: IOException) {
                _homeCollectionState.emit(HomeCollectionState.Error(error = e.message.toString()))
            }
        }
    }

    fun onSearchTextChange(text: String) {
        if (_searchText.value == text) return
        _searchText.value = text
    }

    companion object {
        private const val PHOTOS_PAGE_SIZE = 30
        private const val COLLECTIONS_PAGE_SIZE = 7
        private fun emptyString() = ""
    }
}

sealed class HomeCollectionState {
    data object Idle : HomeCollectionState()
    data object Loading : HomeCollectionState()
    class Error(val error: String) : HomeCollectionState()
    class Success(val collections: List<FeaturedCollection> = emptyList()) : HomeCollectionState()
}