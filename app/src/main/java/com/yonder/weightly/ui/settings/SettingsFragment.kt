package com.yonder.weightly.ui.settings

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentSettingsBinding
import com.yonder.weightly.ui.splash.SplashViewModel
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SettingsFragment : Fragment(R.layout.fragment_settings) {

    private val binding by viewBinding(FragmentSettingsBinding::bind)

    private val viewModel: SplashViewModel by viewModels()

}