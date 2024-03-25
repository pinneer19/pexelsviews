package com.example.pexelsviews.presentation.utils

import com.facebook.shimmer.Shimmer
import com.facebook.shimmer.ShimmerDrawable

private val shimmer = Shimmer.AlphaHighlightBuilder()
    .setDuration(500)
    .setBaseAlpha(0.95f)
    .setHighlightAlpha(0.9f)
    .setDirection(Shimmer.Direction.LEFT_TO_RIGHT)
    .setAutoStart(true)
    .build()
val shimmerDrawable = ShimmerDrawable().apply {
    setShimmer(shimmer)
}