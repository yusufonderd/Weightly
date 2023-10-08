package com.yonder.weightly.ui.setlock

import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentSetLockBinding
import com.yonder.weightly.utils.extensions.showToast
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class SetLockFragment : Fragment(R.layout.fragment_set_lock) {

    private val binding by viewBinding(FragmentSetLockBinding::bind)

    private val viewModel: SetLockViewModel by viewModels()

    private lateinit var menuHost: MenuHost

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initVariables()
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    SetLockViewModel.Event.NavigateToSplash -> {
                        findNavController().navigate(SetLockFragmentDirections.actionNavigateSplash())
                    }
                    is SetLockViewModel.Event.ShowMessage -> {
                        context?.showToast(event.message)
                    }
                }
            }
        }
    }

    private fun initVariables() {
        menuHost = requireActivity()
    }

    override fun onPause() {
        super.onPause()
        menuHost.removeMenuProvider(menuProvider)
    }

    override fun onResume() {
        super.onResume()
        menuHost.addMenuProvider(menuProvider)
    }

    private val menuProvider: MenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.set_lock_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.action_done -> {
                    viewModel.setLock(
                        password = binding.etPassword.text.toString(),
                        passwordAgain = binding.etPasswordAgain.text.toString(),
                        passwordHint = binding.etPasswordHint.text.toString(),
                        recoveryEmail = binding.etEmail.text.toString()
                    )
                    true
                }
                else -> false
            }
        }

    }
}

