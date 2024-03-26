package com.example.pexelsviews.presentation.bookmarks.recyclerview

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.ItemBookmarkBinding
import com.example.pexelsviews.domain.model.Photo


class BookmarksAdapter(private val navigateToDetails: (Int) -> Unit) :
    PagingDataAdapter<Photo, BookmarksAdapter.ViewHolder>(BookmarksDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemBookmarkBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val photo = getItem(position) ?: return
        with(holder.binding) {
            loadImage(imageView, photo.src.medium)
            authorTextView.text = photo.photographer
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
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.broken_image)
                .into(imageView)
        } else {
            Glide.with(context)
                .load(R.drawable.broken_image)
                .into(imageView)
        }
    }

    class ViewHolder(val binding: ItemBookmarkBinding) : RecyclerView.ViewHolder(binding.root)
}

class BookmarksDiffCallback : DiffUtil.ItemCallback<Photo>() {
    override fun areItemsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Photo, newItem: Photo): Boolean {
        return oldItem == newItem
    }
}