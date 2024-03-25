package com.example.pexelsviews.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.bumptech.glide.Glide
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.FragmentHomeBinding
import com.example.pexelsviews.presentation.details.RequestState
import com.example.pexelsviews.presentation.home.recyclerview.PhotosAdapter
import com.example.pexelsviews.presentation.utils.countScan
import com.example.pexelsviews.presentation.utils.shimmerDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by lazy { ViewModelProvider(requireActivity())[HomeViewModel::class.java] }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupPhotosList()
        setupSearchInput()

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeCollectionState.collect { state ->
                when (state) {
                    is HomeCollectionState.Idle -> {}
                    is HomeCollectionState.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }

                    is HomeCollectionState.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        val collections = state.collections
                        //binding.collectionList.
                    }

                    is HomeCollectionState.Error -> {
                        binding.progressIndicator.visibility = View.GONE
//                        binding.photoImage.visibility = View.GONE
//                        binding.textBlock.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun navigateToDetails(photoId: Int) {
        val bundle = Bundle().apply {
            putInt(KEY_PHOTO_ID, photoId)
            putBoolean(KEY_IS_BOOKMARK, false)
        }
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
    }

    private fun setupPhotosList() {
        val adapter = PhotosAdapter(
            navigateToDetails = { photoId -> navigateToDetails(photoId) }
        )
        val columnWidth = resources.getDimensionPixelSize(R.dimen.column_width)
        val screenWidth = resources.displayMetrics.widthPixels
        val spanCount = screenWidth / columnWidth

        val layoutManager = StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        //binding.recyclerView.addItemDecoration(SpacesItemDecoration(8)) // Add inner padding between items
        binding.recyclerView.adapter = adapter

        observePhotos(adapter)
        handleListVisibility(adapter)
    }

    private fun observePhotos(adapter: PhotosAdapter) {
        lifecycleScope.launch {
            viewModel.pager.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun setupSearchInput() {
        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean = false

            override fun onQueryTextChange(query: String): Boolean {
                viewModel.onSearchTextChange(query)
                return true
            }
        })
    }

    private fun handleListVisibility(adapter: PhotosAdapter) = lifecycleScope.launch {
        getRefreshLoadStateFlow(adapter)
            .countScan()
            .collectLatest { (beforePrevious, previous, current) ->
                binding.recyclerView.isInvisible = current is LoadState.Error
                        || previous is LoadState.Error
                        || (beforePrevious is LoadState.Error && previous is LoadState.NotLoading
                        && current is LoadState.Loading)
            }
    }

    private fun getRefreshLoadStateFlow(adapter: PhotosAdapter): Flow<LoadState> {
        return adapter.loadStateFlow.map { it.refresh }
    }

    companion object {
        private const val KEY_PHOTO_ID = "PHOTO_ID"
        private const val KEY_IS_BOOKMARK = "IS_BOOKMARK"
    }
}