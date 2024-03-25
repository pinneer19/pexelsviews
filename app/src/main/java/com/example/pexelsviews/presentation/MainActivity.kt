package com.example.pexelsviews.presentation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.ActivityMainBinding
import com.example.pexelsviews.presentation.home.HomeCollectionState
import com.example.pexelsviews.presentation.home.HomeFragment
import com.example.pexelsviews.presentation.home.HomeViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val homeViewModel by lazy {
        ViewModelProvider(this)[HomeViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        if (savedInstanceState == null) {
            setupSplashScreen()
        }

        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun setupSplashScreen() {

        installSplashScreen().apply {
            setKeepOnScreenCondition {
                homeViewModel.homeCollectionState.value !is HomeCollectionState.Loading &&
                        homeViewModel.homeCollectionState.value !is HomeCollectionState.Idle
            }
            setOnExitAnimationListener { splashScreenView ->
                val backgroundColorAnimator = ValueAnimator.ofArgb(
                    splashScreenView.view.getBackgroundColor(), Color.TRANSPARENT
                ).apply {
                    duration = 200L
                    addUpdateListener { animator ->
                        splashScreenView.view.setBackgroundColor(animator.animatedValue as Int)
                    }
                }
                val iconExitAnimation = ObjectAnimator.ofFloat(
                    splashScreenView.iconView,
                    View.TRANSLATION_X,
                    0f,
                    splashScreenView.view.width.toFloat()
                ).apply {
                    interpolator = AnticipateInterpolator()
                    duration = 500L
                }

                AnimatorSet().apply {
                    play(backgroundColorAnimator).after(iconExitAnimation)
                    doOnEnd { splashScreenView.remove() }
                    start()
                }
            }
        }
    }

    private fun View.getBackgroundColor() =
        (background as? ColorDrawable?)?.color ?: Color.TRANSPARENT
}