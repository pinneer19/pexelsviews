package com.example.pexelsviews.presentation.utils

import android.content.Context
import android.content.res.ColorStateList
import android.util.TypedValue

fun Int.getColorStateList(context: Context): ColorStateList {
    val value = TypedValue()
    context.theme.resolveAttribute(
        this,
        value,
        true
    )
    return ColorStateList.valueOf(value.data)
}

fun Int.getColor(context: Context): Int {
    val value = TypedValue()
    context.theme.resolveAttribute(
        this,
        value,
        true
    )
    return value.data
}