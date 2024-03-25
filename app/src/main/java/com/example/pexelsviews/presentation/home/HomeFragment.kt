package com.example.pexelsviews.presentation.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.isInvisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.FragmentHomeBinding
import com.example.pexelsviews.presentation.home.recyclerview.PhotosAdapter
import com.example.pexelsviews.presentation.utils.countScan
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by lazy { ViewModelProvider(requireActivity())[HomeViewModel::class.java] }
    private var selectedIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setupPhotosList()
        setupSearchInput()

        binding.exploreView.setOnClickListener {
            binding.searchView.setQuery("", true)
            binding.searchView.clearFocus()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.homeCollectionState.collect { state ->
                when (state) {
                    is HomeCollectionState.Idle -> {}
                    is HomeCollectionState.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                        binding.collectionList.visibility = View.GONE
                    }

                    is HomeCollectionState.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        binding.scrollView.visibility = View.VISIBLE
                        binding.collectionList.visibility = View.VISIBLE
                        fillCollectionsView(layoutInflater, state.collections.map { it.title })
                    }

                    is HomeCollectionState.Error -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                        binding.collectionList.visibility = View.GONE
                        binding.recyclerView.visibility = View.GONE
                        binding.errorBlock.visibility = View.VISIBLE
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
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        observePhotos(adapter)
        handleListVisibility(adapter)
    }

    private fun observePhotos(adapter: PhotosAdapter) {
        lifecycleScope.launch {
            viewModel.pager.collectLatest { pagingData ->
                adapter.submitData(pagingData)
                updateEmptyBlock(adapter.itemCount)
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

    private fun fillCollectionsView(inflater: LayoutInflater, collections: List<String>) {

        val linearLayout = binding.collectionList

        collections.forEachIndexed { index, item ->
            val textView =
                inflater.inflate(R.layout.collection_item, linearLayout, false) as TextView
            textView.text = item
            addInnerMargin(textView, index == collections.lastIndex)

            textView.setOnClickListener {
                viewStyleUpdate(it as TextView, index, linearLayout)
                binding.searchView.setQuery(item, true)
            }
            linearLayout.addView(textView)
        }
    }

    private fun addInnerMargin(textView: TextView, isLast: Boolean) {
        val pxMarginEnd = resources.getDimensionPixelSize(R.dimen.collection_margin)
        val params = textView.layoutParams as ViewGroup.MarginLayoutParams
        params.marginEnd = if (isLast) pxMarginEnd * 3 else pxMarginEnd
        textView.layoutParams = params
    }

    private fun viewStyleUpdate(
        textView: TextView,
        index: Int,
        linearLayout: LinearLayout
    ) {
        textView.isSelected = !textView.isSelected
        when (selectedIndex) {
            -1 -> {
                selectedIndex = index
                updateTextViewState(textView)
            }

            index -> {
                selectedIndex = -1
                updateTextViewState(textView)
            }

            else -> {
                val prevSelected = linearLayout.getChildAt(selectedIndex) as TextView
                prevSelected.isSelected = false
                updateTextViewState(prevSelected)
                selectedIndex = index
                updateTextViewState(textView)
            }
        }
    }

    private fun updateTextViewState(textView: TextView) {
        val context = requireContext()
        with(textView) {
            when (isSelected) {
                false -> {
                    backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.lightGray)
                    )
                    setTextColor(ContextCompat.getColor(context, R.color.black))
                    setTextAppearance(R.style.labelMedium)
                }

                true -> {
                    backgroundTintList = ColorStateList.valueOf(
                        ContextCompat.getColor(context, R.color.red)
                    )
                    setTextColor(ContextCompat.getColor(context, R.color.white))
                    setTextAppearance(R.style.titleMedium)
                    setTextSize(TypedValue.COMPLEX_UNIT_SP, 14f)
                }
            }
        }
    }

    private fun setupSearchInput() {
        with(binding.searchView) {
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean = false

                override fun onQueryTextChange(query: String): Boolean {
                    viewModel.onSearchTextChange(query)
                    updateSelectedCollection(query)
                    return true
                }
            })
        }
    }

    private fun updateSelectedCollection(query: String) {
        with(binding.collectionList) {
            for (i in 0 until childCount) {
                val currentView = getChildAt(i) as TextView

                if (currentView.text == query || selectedIndex == i) {
                    viewStyleUpdate(currentView, i, this)
                    return@with
                }
            }
        }
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