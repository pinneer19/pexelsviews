package com.example.pexelsviews.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.cachedIn
import com.example.pexelsviews.domain.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class BookmarksViewModel @Inject constructor(
    bookmarksRepository: BookmarkRepository
) : ViewModel() {
    val pager = bookmarksRepository
        .getBookmarks()
        .cachedIn(viewModelScope)
}