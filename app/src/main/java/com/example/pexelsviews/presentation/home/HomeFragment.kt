package com.example.pexelsviews.presentation.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.FragmentHomeBinding
import com.example.pexelsviews.presentation.home.recyclerview.PhotosAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val viewModel by viewModels<HomeViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        return binding.root
    }

    private fun setupPhotosList() {
        val adapter = PhotosAdapter()

        val columnWidth = resources.getDimensionPixelSize(R.dimen.column_width)
        val screenWidth = resources.displayMetrics.widthPixels
        val spanCount = screenWidth / columnWidth

        val layoutManager = StaggeredGridLayoutManager(spanCount, LinearLayoutManager.VERTICAL)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.adapter = adapter

    }

    private fun observePhotos(adapter: PhotosAdapter) {
        lifecycleScope.launch {
            viewModel.pager.collectLatest { pagingData ->
                adapter.submitData(pagingData)
            }
        }
    }
}