package com.example.pexelsviews.presentation.details

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.pexelsviews.domain.model.Photo
import com.example.pexelsviews.domain.repository.BookmarkRepository
import com.example.pexelsviews.domain.repository.PhotoRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed class RequestState {
    data object Idle : RequestState()
    data class Success(val data: Photo) : RequestState()
    data object Loading : RequestState()
    data object Error : RequestState()
}

class DetailsViewModel @AssistedInject constructor(
    private val photoRepository: PhotoRepository,
    private val bookmarkRepository: BookmarkRepository,
    @Assisted private val id: Int,
    @Assisted private val isBookmark: Boolean
) : ViewModel() {

    private val _photoState = MutableStateFlow<RequestState>(RequestState.Idle)
    val photoState = _photoState.asStateFlow()

    private val _bookmarkState = MutableStateFlow(isBookmark)
    val bookmarkState = _bookmarkState.asStateFlow()

    private var initialState = isBookmark

    init {
        checkBookmarkExistence()
        loadPhotoDetails()
    }

    private fun loadPhotoDetails() {
        viewModelScope.launch {
            try {
                _photoState.emit(RequestState.Loading)
                val photo = photoRepository.getPhotoDetails(id, isBookmark)
                _photoState.emit(RequestState.Success(photo))
            } catch (e: HttpException) {
                _photoState.emit(RequestState.Error)
            } catch (e: IOException) {
                _photoState.emit(RequestState.Error)
            }
        }
    }

    private fun checkBookmarkExistence() {
        viewModelScope.launch {
            val exist = bookmarkRepository.checkBookmarkStatus(id, isBookmark)
            initialState = exist
            _bookmarkState.emit(exist)
        }
    }

    fun downloadPhoto(url: String) {
        photoRepository.downloadPhoto(url)
    }

    fun updateBookmarkState() {
        viewModelScope.launch {
            _bookmarkState.emit(!_bookmarkState.value)
        }
    }

    fun saveBookmarkState() {
        viewModelScope.launch {
            if (initialState != _bookmarkState.value) {
                when (_bookmarkState.value) {
                    true -> {
                        if (_photoState.value is RequestState.Success) {
                            bookmarkRepository.addPhotoToBookmarks((_photoState.value as RequestState.Success).data)
                        }
                    }

                    false -> bookmarkRepository.deleteBookmark(id, isBookmark)
                }
            }
        }
    }

    @AssistedFactory
    interface Factory {
        fun create(id: Int, isBookmark: Boolean): DetailsViewModel
    }

    companion object {
        fun provideDetailsViewModelFactory(
            factory: Factory,
            id: Int,
            isBookmark: Boolean
        ): ViewModelProvider.Factory {
            return object : ViewModelProvider.Factory {
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return factory.create(id, isBookmark) as T
                }
            }
        }
    }
}