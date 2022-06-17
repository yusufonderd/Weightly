package com.yonder.weightly.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.data.repository.WeightRepository
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.ui.home.chart.ChartType
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.extensions.format
import com.yonder.weightly.utils.extensions.orZero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private var weightRepository: WeightRepository,
    private val weightDao: WeightDao
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchInsights()
    }
    private fun fetchAverage() {
        viewModelScope.launch(Dispatchers.IO) {
            weightDao.getAverage().collectLatest { average ->
                _uiState.update {
                    it.copy(
                        averageWeight = "${average?.format(1)}",
                    )
                }
            }
        }
    }

    private fun fetchMax() {
        viewModelScope.launch(Dispatchers.IO) {
            weightDao.getMax().collectLatest { max ->
                _uiState.update {
                    it.copy(
                        maxWeight = "$max"
                    )
                }
            }
        }
    }

    private fun fetchMin() {
        viewModelScope.launch(Dispatchers.IO) {
            weightDao.getMin().collectLatest { min ->
                _uiState.update {
                    it.copy(
                        minWeight = "$min"
                    )
                }
            }
        }
    }

    private fun fetchGoal() {
        val goalWeight = "${Hawk.get(Constants.Prefs.KEY_GOAL_WEIGHT, 0.0)}"
        _uiState.update {
            it.copy(
                goalWeight = goalWeight
            )
        }
    }

    private fun fetchInsights() {
        fetchAverage()
        fetchMax()
        fetchMin()
        fetchGoal()
    }

     fun getWeightHistories() = viewModelScope.launch(Dispatchers.IO) {
        weightRepository.invoke().collectLatest { weightHistories ->
            _uiState.update {
                it.copy(
                    histories = weightHistories,
                    startWeight = "${weightHistories.firstOrNull()?.formattedValue}",
                    shouldShowInsightView = weightHistories.size > 1,
                    currentWeight = "${weightHistories.lastOrNull()?.formattedValue}",
                    reversedHistories = weightHistories.asReversed().take(WEIGHT_LIMIT_FOR_HOME),
                    shouldShowAllWeightButton = weightHistories.size > WEIGHT_LIMIT_FOR_HOME,
                    barEntries = weightHistories.mapIndexed { index, weight ->
                        BarEntry(index.toFloat(), weight?.value.orZero())
                    },
                    shouldShowLimitLine = Hawk.get(Constants.Prefs.KEY_CHART_LIMIT_LINE,true),
                    chartType = ChartType.findValue(Hawk.get(Constants.Prefs.KEY_CHART_TYPE, 0)),
                    shouldShowEmptyView = weightHistories.isEmpty()
                )
            }
        }
    }

    fun changeChartType(chartType: ChartType) {
        val currentChartType = ChartType.findValue(Hawk.get(Constants.Prefs.KEY_CHART_TYPE, 0))
        if (chartType != currentChartType) {
            Hawk.put(Constants.Prefs.KEY_CHART_TYPE, chartType.value)
            _uiState.update {
                it.copy(
                    chartType = chartType
                )
            }
        }
    }

    data class UiState(
        var maxWeight: String? = null,
        var minWeight: String? = null,
        var averageWeight: String? = null,
        var startWeight: String? = null,
        var currentWeight: String? = null,
        var goalWeight: String? = null,
        var histories: List<WeightUIModel?> = emptyList(),
        var reversedHistories: List<WeightUIModel?> = emptyList(),
        var barEntries: List<BarEntry> = emptyList(),
        var shouldShowEmptyView: Boolean = false,
        var shouldShowAllWeightButton: Boolean = false,
        var shouldShowInsightView: Boolean = false,
        var shouldShowLimitLine : Boolean = false,
        var chartType: ChartType = ChartType.LINE
    )

    companion object {
        const val WEIGHT_LIMIT_FOR_HOME = 5
    }
}