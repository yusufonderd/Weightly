package com.yonder.weightly.ui.history

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentHistoryBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.uicomponents.EmptyView
import com.yonder.weightly.utils.viewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HistoryFragment : Fragment(R.layout.fragment_history) {

    private val viewModel: HistoryViewModel by viewModels()

    private val binding by viewBinding(FragmentHistoryBinding::bind)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        setHasOptionsMenu(true)
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() = with(binding) {
        composeView.setContent {
            val uiState by viewModel.uiState.collectAsState()
            if (uiState.shouldShowEmptyView) {
                EmptyView(text = stringResource(id = R.string.title_no_weight))
            } else {
                HistoryItemsContent(
                    list = uiState.histories,
                    onClickWeight = ::onClickWeight
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.getWeightHistories()
    }

    override fun onStop() {
        super.onStop()
        viewModel.cancelJobs()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    private fun onClickWeight(weight: WeightUIModel) {
        findNavController().navigate(
            HistoryFragmentDirections.actionNavigateAddWeight(
                weight = weight,
                selectedDate = null
            )
        )
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                findNavController().navigate(
                    HistoryFragmentDirections.actionNavigateAddWeight(
                        weight = null,
                        selectedDate = null
                    )
                )
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}