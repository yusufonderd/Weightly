package com.yonder.weightly.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.yonder.weightly.R
import com.yonder.weightly.utils.extensions.safeNavigate
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {

    private val viewModel: SplashViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        viewModel.startSplash()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    SplashViewModel.Event.NavigateToOnBoardingScreen -> {
                        safeNavigate(SplashFragmentDirections.actionNavigateOnBoarding())
                    }
                    SplashViewModel.Event.NavigateToHome -> {
                        safeNavigate(SplashFragmentDirections.actionNavigateHome())
                    }
                    SplashViewModel.Event.NavigateToOpenLockScreen -> {
                        safeNavigate(SplashFragmentDirections.actionNavigateToOpenLock())
                    }
                }
            }
        }
    }
}