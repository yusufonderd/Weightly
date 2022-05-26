package com.yonder.weightly.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.yonder.statelayout.State
import com.yonder.weightly.R
import com.yonder.weightly.databinding.FragmentHomeBinding
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.ui.home.adapter.WeightHistoryAdapter
import com.yonder.weightly.ui.home.adapter.WeightItemDecorator
import com.yonder.weightly.ui.home.chart.WeightMarkerView
import com.yonder.weightly.ui.home.chart.WeightValueFormatter
import com.yonder.weightly.ui.home.chart.XAxisValueDateFormatter
import com.yonder.weightly.utils.extensions.EMPTY
import com.yonder.weightly.utils.extensions.orZero
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
        observe()
    }

    private fun observe() {
        lifecycleScope.launchWhenCreated {
            viewModel.uiState.collect(::setUIState)
        }
    }

    private fun setUIState(uiState: HomeViewModel.UiState) {
        if (uiState.shouldShowEmptyView) {
            binding.stateLayout.setState(State.EMPTY)
        } else {
            binding.stateLayout.setState(State.CONTENT)
            adapterWeightHistory.submitList(uiState.reversedHistories)
            setChartData(histories = uiState.histories, barEntries = uiState.barEntries)
        }
    }

    private fun initViews() {
        initWeightRecyclerview()
        initBarChart()
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

    private fun initBarChart() = with(binding.barChart) {
        legend.isEnabled = false
        axisLeft.axisMinimum = 0.0f
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        // Disable grid lines
        xAxis.setDrawGridLines(false)
        axisLeft.setDrawGridLines(false)
        axisRight.setDrawGridLines(false)
        //Disable zoom
        isDoubleTapToZoomEnabled = false
        setPinchZoom(false)
        description = Description().apply {
            text = String.EMPTY
        }
    }

    private fun onClickWeight(weight: WeightUIModel) {
        findNavController().navigate(HomeFragmentDirections.actionNavigateAddWeight(weight))
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                findNavController().navigate(HomeFragmentDirections.actionNavigateAddWeight(null))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setChartData(histories: List<WeightUIModel?>, barEntries: List<BarEntry>) {
        val set1 = BarDataSet(barEntries, String.EMPTY)
        set1.valueFormatter = WeightValueFormatter(histories)
        set1.valueTextSize = 9f
        val xAxis = binding.barChart.xAxis
        xAxis.labelCount = histories.size
        xAxis.valueFormatter = XAxisValueDateFormatter(histories)
        set1.color = Color.BLUE
        val dataSets: java.util.ArrayList<IBarDataSet> = ArrayList()
        dataSets.add(set1)
        val data = BarData(dataSets)
        binding.barChart.data = data
        //Set marker view
        val markerView = WeightMarkerView(requireContext(), histories)
        markerView.chartView = binding.barChart
        binding.barChart.marker = markerView

        binding.barChart.invalidate()
    }

}