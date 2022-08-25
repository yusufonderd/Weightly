package com.yonder.weightly.ui.splash

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentSplashBinding
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SplashFragment : Fragment(R.layout.fragment_splash) {
    private val binding by viewBinding(FragmentSplashBinding::bind)

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
                        findNavController().navigate(SplashFragmentDirections.actionNavigateOnBoarding())
                    }
                    SplashViewModel.Event.NavigateToHome -> {
                        findNavController().navigate(SplashFragmentDirections.actionNavigateHome())
                    }
                }
            }
        }
    }
}