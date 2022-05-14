package com.yonder.weightly.ui.home

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentHomeBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.ui.home.adapter.WeightHistoryAdapter
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private val binding by viewBinding(FragmentHomeBinding::bind)

    private val viewModel: HomeViewModel by viewModels()

   private val adapterWeightHistory: WeightHistoryAdapter by lazy {
        WeightHistoryAdapter(::onClickWeight)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        observe()
    }

    private fun observe(){
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect(::setUIState)
        }
    }

    private fun setUIState(uiState: HomeViewModel.UiState){
        adapterWeightHistory.submitList(uiState.histories)
    }

    private fun initViews() = with(binding){
        rvWeightHistory.adapter = adapterWeightHistory
    }

    private fun onClickWeight(weight: WeightUIModel) {

    }

}