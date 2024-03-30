package com.example.pexelsviews.presentation.bookmarks

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.FragmentBookmarksBinding
import com.example.pexelsviews.presentation.bookmarks.recyclerview.BookmarksAdapter
import com.example.pexelsviews.presentation.utils.setupExploreTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BookmarksFragment : Fragment() {

    private lateinit var binding: FragmentBookmarksBinding
    private val viewModel by viewModels<BookmarksViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentBookmarksBinding.inflate(inflater, container, false)
        setupPhotosList()

        val bottomBar = requireActivity().findViewById<FrameLayout>(R.id.bottomNavigationView)
        bottomBar.visibility = View.VISIBLE

        return binding.root
    }

    private fun setupPhotosList() {
        val adapter = BookmarksAdapter(
            navigateToDetails = { photoId -> navigateToDetails(photoId) }
        )

        adapter.addOnPagesUpdatedListener {
            updateEmptyBlock(adapter.itemCount)
        }

        val columnWidth = resources.getDimensionPixelSize(R.dimen.column_width)
        val screenWidth = resources.displayMetrics.widthPixels
        val spanCount = screenWidth / columnWidth

        val layoutManager = StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        setupExploreTextView(binding.exploreView) {
            Navigation.findNavController(binding.root)
                .navigate(R.id.action_bookmarksFragment_to_homeFragment)
        }

        observePhotos(adapter)
    }

    private fun navigateToDetails(photoId: Int) {
        val bundle = Bundle().apply {
            putInt(KEY_PHOTO_ID, photoId)
            putBoolean(KEY_IS_BOOKMARK, true)
        }
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_bookmarksFragment_to_detailsFragment, bundle)
    }

    private fun observePhotos(adapter: BookmarksAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pager.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun updateEmptyBlock(itemsCount: Int) {
        if (itemsCount == 0) {
            binding.emptyBlock.visibility = View.VISIBLE
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyBlock.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
        }
    }


    companion object {
        private const val KEY_PHOTO_ID = "PHOTO_ID"
        private const val KEY_IS_BOOKMARK = "IS_BOOKMARK"
    }
}