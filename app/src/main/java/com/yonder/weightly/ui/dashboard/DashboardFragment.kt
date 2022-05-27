package com.yonder.weightly.ui.dashboard

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentDashboardBinding
import com.yonder.weightly.uicomponents.InfoCardUIModel
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DashboardFragment : Fragment(R.layout.fragment_dashboard) {

    private val binding by viewBinding(FragmentDashboardBinding::bind)

    private val viewModel: DashboardViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        observe()
        viewModel.fetchInsights()
    }

    private fun observe() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect(::setUIState)
        }
    }

    private fun setUIState(uiState: DashboardViewModel.UiState) {
        binding.infoCardAverage.render(
            InfoCardUIModel(
                title = uiState.averageWeight.orEmpty(),
                description = R.string.title_average_weight,
                backgroundColor = R.color.orange
            )
        )
        binding.infoCardMax.render(
            InfoCardUIModel(
                title = uiState.maxWeight.orEmpty(),
                description = R.string.title_max_weight,
                backgroundColor = R.color.red
            )
        )
        binding.infoCardMin.render(
            InfoCardUIModel(
                title = uiState.minWeight.orEmpty(),
                description = R.string.title_min_weight,
                backgroundColor = R.color.green
            )
        )

    }


}