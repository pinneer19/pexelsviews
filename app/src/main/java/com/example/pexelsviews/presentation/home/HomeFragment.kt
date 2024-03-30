package com.example.pexelsviews.presentation.home

import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.FragmentHomeBinding
import com.example.pexelsviews.presentation.home.recyclerview.PhotosAdapter
import com.example.pexelsviews.presentation.utils.ConnectivityObserver.Status
import com.example.pexelsviews.presentation.utils.countScan
import com.example.pexelsviews.presentation.utils.getColor
import com.example.pexelsviews.presentation.utils.getColorStateList
import com.example.pexelsviews.presentation.utils.setupExploreTextView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch


@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel>()
    private var selectedIndex: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        setupPhotosList()
        setupSearchInput()
        setupNetworkObserver()

        val bottomBar = requireActivity().findViewById<FrameLayout>(R.id.bottomNavigationView)
        bottomBar.visibility = View.VISIBLE

        setupExploreTextView(binding.tryAgainView) {
            if (viewModel.homeCollectionState.value is HomeCollectionState.Error) {
                viewModel.loadCollections()
            }
            (binding.recyclerView.adapter as PhotosAdapter).retry()
        }

        setupExploreTextView(binding.exploreView) {
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
                        binding.scrollView.visibility = View.VISIBLE
                        binding.collectionList.visibility = View.VISIBLE
                        fillCollectionsView(layoutInflater, state.collections.map { it.title })
                    }

                    is HomeCollectionState.Error -> {
                        Toast.makeText(
                            requireContext(), "Check your internet connection", Toast.LENGTH_SHORT
                        ).show()
                        binding.collectionList.visibility = View.GONE
                    }
                }
            }
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
                println("$index $selectedIndex")
                if (index == selectedIndex) {
                    binding.searchView.setQuery("", true)
                    binding.searchView.clearFocus()
                } else {
                    binding.searchView.setQuery(item, true)
                }
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
        textView: TextView, index: Int, linearLayout: LinearLayout
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

                    val stateList =
                        com.google.android.material.R.attr.colorPrimaryContainer.getColorStateList(
                            requireContext()
                        )
                    backgroundTintList = stateList

                    val textColor =
                        com.google.android.material.R.attr.colorOnPrimaryContainer.getColor(
                            requireContext()
                        )
                    setTextColor(textColor)
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

    private fun setupPhotosList() {
        val adapter = PhotosAdapter(navigateToDetails = { photoId -> navigateToDetails(photoId) })

        adapter.addOnPagesUpdatedListener {
            updateEmptyBlock(adapter.itemCount)
        }

        val layoutManager = StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL)
        layoutManager.gapStrategy = StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS

        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

        observePhotos(adapter)
        observeLoadState(adapter)
        handleScrollingToTopWhenSearching(adapter)
    }

    private fun navigateToDetails(photoId: Int) {
        val bundle = Bundle().apply {
            putInt(KEY_PHOTO_ID, photoId)
            putBoolean(KEY_IS_BOOKMARK, false)
        }
        Navigation.findNavController(binding.root)
            .navigate(R.id.action_homeFragment_to_detailsFragment, bundle)
    }

    private fun observePhotos(adapter: PhotosAdapter) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.pager.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }

    private fun handleScrollingToTopWhenSearching(adapter: PhotosAdapter) = lifecycleScope.launch {
        getRefreshLoadStateFlow(adapter).countScan(count = 2)
            .collectLatest { (previousState, currentState) ->
                if (previousState is LoadState.Loading && currentState is LoadState.NotLoading) {
                    binding.recyclerView.scrollToPosition(0)
                }
            }
    }

    private fun getRefreshLoadStateFlow(adapter: PhotosAdapter): Flow<LoadState> {
        return adapter.loadStateFlow.map { it.refresh }
    }


    @OptIn(FlowPreview::class)
    private fun observeLoadState(adapter: PhotosAdapter) {
        lifecycleScope.launch {
            adapter.loadStateFlow.debounce(200).collectLatest { state ->
                if (state.refresh is LoadState.Loading || state.append is LoadState.Loading) {
                    binding.progressIndicator.visibility = View.VISIBLE
                } else if (state.append is LoadState.Error) {
                    Toast.makeText(
                        requireContext(), "Check your internet connection!", Toast.LENGTH_SHORT
                    ).show()
                } else {
                    if (binding.errorBlock.visibility == View.VISIBLE && viewModel.homeCollectionState.value !is HomeCollectionState.Error) {
                        binding.errorBlock.visibility = View.GONE
                    }
                    binding.progressIndicator.visibility = View.GONE
                }
            }
        }
    }

    private fun updateEmptyBlock(itemsCount: Int) {
        if (itemsCount == 0) {
            if (viewModel.homeCollectionState.value is HomeCollectionState.Error) {
                binding.errorBlock.visibility = View.VISIBLE
            } else {
                binding.emptyBlock.visibility = View.VISIBLE
            }
            binding.recyclerView.visibility = View.GONE
        } else {
            binding.emptyBlock.visibility = View.GONE
            binding.recyclerView.visibility = View.VISIBLE
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

                if (currentView.text == query) {
                    viewStyleUpdate(currentView, i, this)
                    return@with
                }
            }
            if (selectedIndex != -1) {
                viewStyleUpdate(getChildAt(selectedIndex) as TextView, selectedIndex, this)
                return@with
            }
        }
    }

    private fun setupNetworkObserver() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.networkObserver.observe()
                .countScan(2)
                .collectLatest { (previousState, currentState) ->
                    if (previousState == Status.AVAILABLE && currentState == Status.LOST) {
                        Toast.makeText(requireContext(), "Network is lost!", Toast.LENGTH_LONG)
                            .show()
                    } else if (previousState == Status.LOST && currentState == Status.AVAILABLE) {
                        setupRefreshDialog {
                            (binding.recyclerView.adapter as PhotosAdapter).refresh()
                        }
                    }
                }
        }
    }

    private fun setupRefreshDialog(onRefreshList: () -> Unit) {
        val dialog = AlertDialogFragment(onRefreshList)
        dialog.show(childFragmentManager, "alertDialog")
    }

    companion object {
        private const val KEY_PHOTO_ID = "PHOTO_ID"
        private const val KEY_IS_BOOKMARK = "IS_BOOKMARK"
    }
}