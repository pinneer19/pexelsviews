package com.example.pexelsviews.presentation.utils

import android.content.Context
import android.util.DisplayMetrics

fun Int.dpToPx(context: Context): Float = (this * context.resources.displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)
