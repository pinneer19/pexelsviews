package com.example.pexelsviews.presentation.home.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.navigation.NavController
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.ItemPhotoBinding
import com.example.pexelsviews.domain.model.Photo
import com.example.pexelsviews.presentation.utils.shimmerDrawable
import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

class PhotosAdapter(private val navigateToDetails: (Int) -> Unit) : PagingDataAdapter<Photo, PhotosAdapter.ViewHolder>(PhotosDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = getItem(position) ?: return
        with(holder.binding) {
            loadImage(imageView, photo.src.medium)
            imageView.setOnClickListener {
                navigateToDetails(photo.id)
            }
        }
    }

    private fun loadImage(imageView: ImageView, url: String) {
        val context = imageView.context

        if (url.isNotBlank()) {
            Glide.with(context)
                .load(url)
                .placeholder(shimmerDrawable)
                .error(R.drawable.broken_image)
                .into(imageView)
        } else {
            Glide.with(context)
                .load(R.drawable.broken_image)
                .into(imageView)
        }
    }

    class ViewHolder(val binding: ItemPhotoBinding) : RecyclerView.ViewHolder(binding.root)
}

class PhotosDiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}