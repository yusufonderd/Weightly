package com.yonder.weightly.ui.openlock

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentOpenLockBinding
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.extensions.safeNavigate
import com.yonder.weightly.utils.extensions.showToast
import com.yonder.weightly.utils.setSafeOnClickListener
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class OpenLockFragment : Fragment(R.layout.fragment_open_lock) {

    private val binding by viewBinding(FragmentOpenLockBinding::bind)

    private val viewModel: OpenLockViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
    }


    private fun observe() {
        lifecycleScope.launchWhenStarted {
            viewModel.eventsFlow.collect { event ->
                when (event) {
                    OpenLockViewModel.Event.NavigateToHome -> {
                        safeNavigate(OpenLockFragmentDirections.actionNavigateHome())
                    }
                    is OpenLockViewModel.Event.ShowMessage -> {
                        context?.showToast(event.message)
                    }
                    is OpenLockViewModel.Event.Message -> {
                        context?.showToast(event.message)
                    }
                }
            }
        }
    }

    private fun initViews() = with(binding) {
        btnContinue.setSafeOnClickListener {
            viewModel.checkPassword(password = etPassword.text.toString())
        }
        btnForgotPassword.setSafeOnClickListener {
            val hint = String.format(
                getString(R.string.hint),
                Hawk.get(Constants.Prefs.APP_LOCK_HINT)
            )
            val alertBuilder = MaterialAlertDialogBuilder(requireContext())
            alertBuilder.setTitle(hint)
            alertBuilder.setMessage(R.string.send_password_to_recovery_email)
            alertBuilder.setNegativeButton(R.string.no) { _, _ -> }
            alertBuilder.setPositiveButton(R.string.yes) { _, _ ->
                viewModel.forgotPassword()
            }
            alertBuilder.show()

        }
    }

}