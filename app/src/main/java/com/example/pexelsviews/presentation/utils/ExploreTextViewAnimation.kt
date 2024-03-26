package com.example.pexelsviews.presentation.utils

import android.annotation.SuppressLint
import android.view.MotionEvent
import android.view.animation.Animation
import android.view.animation.ScaleAnimation
import android.widget.TextView

@SuppressLint("ClickableViewAccessibility")
fun setupExploreTextView(textView: TextView, onClick: () -> Unit) {

    textView.setOnClickListener {
        onClick()
    }

    textView.setOnTouchListener { v, event ->
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                val scaleDownAnimation = ScaleAnimation(
                    1f, 0.9f,
                    1f, 0.9f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                )
                scaleDownAnimation.duration = 200
                scaleDownAnimation.fillAfter = true
                v?.startAnimation(scaleDownAnimation)
            }

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                val scaleUpAnimation = ScaleAnimation(
                    0.8f, 1f,
                    0.8f, 1f,
                    Animation.RELATIVE_TO_SELF, 0.5f,
                    Animation.RELATIVE_TO_SELF, 0.5f
                )
                scaleUpAnimation.duration = 200
                scaleUpAnimation.fillAfter = true
                v?.startAnimation(scaleUpAnimation)
            }
        }
        false
    }
}