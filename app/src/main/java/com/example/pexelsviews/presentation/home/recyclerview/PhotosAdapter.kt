package com.example.pexelsviews.presentation.home.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.ItemPhotoBinding
import com.example.pexelsviews.domain.model.Photo
import com.google.android.material.imageview.ShapeableImageView

class PhotosAdapter : PagingDataAdapter<Photo, PhotosAdapter.ViewHolder>(PhotosDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemPhotoBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = getItem(position) ?: return
        with(holder.binding) {
            loadImage(imageView, photo.src.medium)
        }
    }

    private fun loadImage(imageView: ShapeableImageView, url: String) {
        val context = imageView.context
        if (url.isNotBlank()) {
            Glide.with(context)
                .load(url)
                .placeholder(R.color.lightGray)
                .into(imageView)
        } else {
            Glide.with(context)
                .load(R.color.lightGray)
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