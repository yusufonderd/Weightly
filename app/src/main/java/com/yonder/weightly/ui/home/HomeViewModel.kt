package com.yonder.weightly.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.domain.usecase.GetAllWeights
import com.yonder.weightly.domain.usecase.GetUserGoal
import com.yonder.weightly.utils.enums.ChartType
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.extensions.orZero
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private var getAllWeights: GetAllWeights ,
    private val weightDao: WeightDao,
    private val getUserGoal: GetUserGoal
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    init {
        fetchInsights()
    }

    private fun fetchInsights() {
        viewModelScope.launch(Dispatchers.IO) {
            combine(weightDao.getMax(),weightDao.getMin(),weightDao.getAvg()){ max, min, avg ->
                _uiState.update {
                    it.copy(
                        minWeight = "$min",
                        maxWeight = "$max",
                        averageWeight = "$avg"
                    )
                }
            }.stateIn(this)
        }
    }

     fun fetchHome() = viewModelScope.launch(Dispatchers.IO) {
        getAllWeights().collectLatest { weightHistories ->
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
                    userGoal = getUserGoal(),
                    shouldShowLimitLine = Hawk.get(Constants.Prefs.KEY_CHART_LIMIT_LINE,true),
                    chartType = ChartType.findValue(Hawk.get(Constants.Prefs.KEY_CHART_TYPE, 0)),
                    shouldShowEmptyView = weightHistories.isEmpty(),
                    goalWeight = "${Hawk.get(Constants.Prefs.KEY_GOAL_WEIGHT, 0.0)}"
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
        var chartType: ChartType = ChartType.LINE,
        var userGoal: String ? = null
    )

    companion object {
        const val WEIGHT_LIMIT_FOR_HOME = 5
    }
}