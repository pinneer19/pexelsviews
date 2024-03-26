package com.example.pexelsviews.presentation

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI.setupWithNavController
import com.example.pexelsviews.R
import com.example.pexelsviews.databinding.ActivityMainBinding
import com.example.pexelsviews.presentation.utils.BackPressHandler
import com.example.pexelsviews.presentation.utils.dpToPx
import com.example.pexelsviews.presentation.utils.getTopFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

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

    override fun onBackPressed() {
        if (!isTopFragmentConsumedBackPress()) {
            onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun isTopFragmentConsumedBackPress(): Boolean {
        return getTopFragment<BackPressHandler>()?.onBackPressed() ?: false
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

    companion object {
        private const val ANIMATION_DURATION = 300L
    }
}