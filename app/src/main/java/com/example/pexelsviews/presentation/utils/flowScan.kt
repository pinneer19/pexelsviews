package com.example.pexelsviews.presentation.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.scan

fun <T> Flow<T>.countScan(count: Int = 3): Flow<List<T?>> {
    val items = List<T?>(count) { null }
    return this.scan(items) { previous, value -> previous.drop(1) + value }
}