package com.example.pexelsviews.presentation

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.animation.AnticipateInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.ActivityMainBinding
import com.example.pexelsviews.presentation.home.HomeCollectionState
import com.example.pexelsviews.presentation.home.HomeFragment
import com.example.pexelsviews.presentation.home.HomeViewModel
import com.example.pexelsviews.presentation.utils.dpToPx
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController
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

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        setupBottomNavigationIndicator(navController)
        setupWithNavController(binding.bottomNavigationView, navController)
        binding.bottomNavigationView.itemIconTintList = null

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

    /*
        Math calculation :3
        considering 2 by 3 relation spaces (mean between icons and outside)
        and size of icon - 24dp
    */
    private fun setupBottomNavigationIndicator(navController: NavController) {
        val displayMetrics = resources.displayMetrics

        val itemWidth = displayMetrics.widthPixels.toFloat() / 7

        val translationX = itemWidth * 2f - 12.dpToPx(this)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.homeFragment -> {
                    binding.indicatorView
                        .animate()
                        .translationX(translationX)
                        .duration = ANIMATION_DURATION

                }

                R.id.bookmarksFragment -> {
                    binding.indicatorView
                        .animate()
                        .translationX(
                            displayMetrics.widthPixels.toFloat() - translationX - 24.dpToPx(this)
                        )
                        .duration = ANIMATION_DURATION
                }
            }
        }
    }

    private fun View.getBackgroundColor() =
        (background as? ColorDrawable?)?.color ?: Color.TRANSPARENT

    companion object {
        private const val ICON_AMOUNT = 2
        private const val DP_OFFSET = 7
        private const val ANIMATION_DURATION = 300L
    }
}