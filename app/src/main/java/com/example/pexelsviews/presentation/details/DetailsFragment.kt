package com.example.pexelsviews.presentation.details

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.Context
import android.content.IntentFilter
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.FrameLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.FragmentDetailsBinding
import com.example.pexelsviews.domain.model.Photo
import com.example.pexelsviews.presentation.utils.DownloadBroadcastReceiver
import com.example.pexelsviews.presentation.utils.setupExploreTextView
import com.google.android.material.internal.ThemeEnforcement.obtainStyledAttributes
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

    private val viewModelFactory by lazy {
        DetailsViewModel.provideDetailsViewModelFactory(
            factoryInstance,
            photoId ?: DEFAULT_ID,
            isBookmark ?: DEFAULT_BOOL
        )
    }

    private val viewModel: DetailsViewModel by lazy {
        ViewModelProvider(this, viewModelFactory)[DetailsViewModel::class.java]
    }
    private val downloadReceiver = DownloadBroadcastReceiver()

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
            viewModel.saveBookmarkState()
            Navigation.findNavController(binding.root).popBackStack()
        }
        binding.downloadButton.setOnClickListener {
            viewModel.downloadPhoto(photo?.src?.original ?: "")
        }

        setupExploreTextView(binding.exploreTitle) {
            viewModel.saveBookmarkState()

            Navigation.findNavController(binding.root)
                .navigate(R.id.action_detailsFragment_to_homeFragment)
        }
        setupBookmarkButton()

        val bottomBar = requireActivity().findViewById<FrameLayout>(R.id.bottomNavigationView)
        bottomBar.visibility = View.GONE
        setupBroadcastReceiver(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        handlePhotoState()
    }

    override fun onDestroyView() {
        requireContext().unregisterReceiver(downloadReceiver)
        super.onDestroyView()
    }

    private fun setupBookmarkButton() {
        updateBookmarkIcon()
        binding.bookmarkButton.setOnClickListener {
            viewModel.updateBookmarkState()
            updateBookmarkIcon()
        }

    }

    private fun updateBookmarkIcon() {
        var tint: ColorStateList? = null
        val imgResource = if (viewModel.bookmarkState.value) {
            R.drawable.ic_bookmark_active
        } else {
            val value = TypedValue()
            requireContext().theme.resolveAttribute(
                com.google.android.material.R.attr.colorOnPrimaryContainer,
                value,
                true
            )
            tint = ColorStateList.valueOf(value.data)
            R.drawable.ic_bookmark
        }
        binding.bookmarkButton.setImageResource(imgResource)
        binding.bookmarkButton.imageTintList = tint
    }

    private fun handlePhotoState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.photoState.collect { state ->
                when (state) {
                    is RequestState.Idle -> {}
                    is RequestState.Loading -> {
                        binding.progressIndicator.visibility = View.VISIBLE
                    }

                    is RequestState.Success -> {
                        photo = state.data

                        binding.authorName.visibility = View.VISIBLE
                        binding.authorName.text = photo?.photographer

                        Glide.with(requireContext())
                            .load(photo?.src?.original)
                            .placeholder(R.drawable.placeholder)
                            .listener(object : RequestListener<Drawable> {
                                override fun onLoadFailed(
                                    e: GlideException?,
                                    model: Any?,
                                    target: Target<Drawable>,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    binding.progressIndicator.visibility = View.GONE
                                    makeToast("Error while loading image")
                                    return false
                                }

                                override fun onResourceReady(
                                    resource: Drawable,
                                    model: Any,
                                    target: Target<Drawable>?,
                                    dataSource: DataSource,
                                    isFirstResource: Boolean
                                ): Boolean {
                                    binding.progressIndicator.visibility = View.GONE
                                    return false
                                }
                            })
                            .into(binding.photoImage)
                    }

                    is RequestState.Error -> {
                        binding.progressIndicator.visibility = View.GONE
                        binding.photoImage.visibility = View.GONE
                        binding.textBlock.visibility = View.VISIBLE
                        binding.bookmarkButton.visibility = View.GONE
                        binding.downloadButton.visibility = View.GONE
                        binding.textView.visibility = View.GONE
                    }
                }
            }
        }
    }

    private fun makeToast(message: String) {
        Toast.makeText(
            requireContext(),
            message,
            Toast.LENGTH_SHORT
        ).show()
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