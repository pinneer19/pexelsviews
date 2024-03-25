package com.example.pexelsviews.presentation.details

import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.example.pexelsviews.databinding.FragmentDetailsBinding
import com.example.pexelsviews.domain.model.Photo
import com.example.pexelsviews.presentation.utils.DownloadBroadcastReceiver
import com.example.pexelsviews.presentation.utils.shimmerDrawable
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class DetailsFragment : Fragment() {

    @Inject
    lateinit var factoryInstance: DetailsViewModel.Factory

    private lateinit var binding: FragmentDetailsBinding

    private var photoId: Int? = null
    private var isBookmark: Boolean? = null
    private var photo: Photo? = null

    private val viewModelFactory =
        DetailsViewModel.provideDetailsViewModelFactory(
            factoryInstance,
            photoId ?: DEFAULT_ID,
            isBookmark ?: DEFAULT_BOOL
        )

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[DetailsViewModel::class.java]
    }
    private val downloadReceiver = DownloadBroadcastReceiver(
        onUpdate = { state -> viewModel.updateDownloadState(state) }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            photoId = it.getInt(KEY_PHOTO_ID)
            isBookmark = it.getBoolean(KEY_IS_BOOKMARK)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailsBinding.inflate(inflater, container, false)

        binding.backButton.setOnClickListener {
            Navigation.findNavController(binding.root).popBackStack()
        }
        binding.downloadButton.setOnClickListener {
            viewModel.downloadPhoto(photo?.src?.original ?: "")
        }
        setupBroadcastReceiver(requireContext())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photoState.collect { state ->
                when (state) {
                    is RequestState.Idle -> {}
                    is RequestState.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }

                    is RequestState.Success -> {
                        binding.progressIndicator.visibility = View.GONE
                        photo = state.data
                        Glide.with(requireContext())
                            .load(photo?.src?.original)
                            .placeholder(shimmerDrawable)
                            .into(binding.photoImage)
                    }

                    is RequestState.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        binding.photoImage.visibility = View.GONE
                        binding.textBlock.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        requireContext().unregisterReceiver(downloadReceiver)
        super.onDestroyView()
    }


    private fun setupBroadcastReceiver(context: Context) {

        val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            context.registerReceiver(downloadReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            context.registerReceiver(downloadReceiver, filter)
        }
    }

    companion object {
        private const val KEY_PHOTO_ID = "PHOTO_ID"
        private const val KEY_IS_BOOKMARK = "IS_BOOKMARK"
        private const val DEFAULT_ID = -1
        private const val DEFAULT_BOOL = false
    }
}