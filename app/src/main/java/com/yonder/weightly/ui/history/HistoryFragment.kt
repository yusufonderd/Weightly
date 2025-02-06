package com.yonder.weightly.ui.history

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.DividerItemDecoration
import com.yonder.statelayout.State
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentHistoryBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.ui.history.adapter.WeightHistoryAdapter
import com.yonder.weightly.ui.history.adapter.WeightItemDecorator
import com.yonder.weightly.utils.extensions.safeNavigate
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) {

    private val viewModel: HistoryViewModel by viewModels()

    private val binding by viewBinding(FragmentHistoryBinding::bind)

    private val adapterWeightHistory: WeightHistoryAdapter by lazy {
        WeightHistoryAdapter(::onClickWeight)
    }

    private lateinit var menuHost: MenuHost

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
    }

    private fun initViews() {
        menuHost = requireActivity()
        initWeightRecyclerview()
    }

    private fun initWeightRecyclerview() = with(binding.rvWeightHistory) {
        adapter = adapterWeightHistory
        addItemDecoration(WeightItemDecorator(requireContext()))
        addItemDecoration(
            DividerItemDecoration(
                context,
                DividerItemDecoration.VERTICAL
            )
        )
    }

    private fun observe() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect(::setUIState)
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getWeightHistories()
        menuHost.addMenuProvider(menuProvider)
    }

    override fun onStop() {
        super.onStop()
        viewModel.cancelJobs()
        menuHost.removeMenuProvider(menuProvider)
    }

    private fun setUIState(uiState: HistoryViewModel.UiState) = with(binding) {
        if (uiState.shouldShowEmptyView) {
            stateLayout.setState(State.EMPTY)
        } else {
            stateLayout.setState(State.CONTENT)
            adapterWeightHistory.submitList(uiState.histories)
        }
    }

    private fun onClickWeight(weight: WeightUIModel) {
        safeNavigate(
            HistoryFragmentDirections.actionNavigateAddWeight(
                weight = weight,
                selectedDate = null
            )
        )
    }

    private val menuProvider: MenuProvider = object : MenuProvider {
        override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
            menuInflater.inflate(R.menu.history_menu, menu)
        }

        override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
            return when (menuItem.itemId) {
                R.id.action_add -> {
                    safeNavigate(
                        HistoryFragmentDirections.actionNavigateAddWeight(
                            weight = null,
                            selectedDate = null
                        )
                    )
                    true
                }

                else -> false
            }
        }
    }

}