package com.example.pexelsviews.presentation.utils

import androidx.fragment.app.FragmentActivity
import com.example.pexelsviews.R

interface BackPressHandler {
    fun onBackPressed(): Boolean
}

inline fun <reified T> FragmentActivity.getTopFragment(): T? {

    val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView)
    val currentFragment = navHostFragment?.childFragmentManager?.fragments?.firstOrNull()
    return currentFragment as? T
}