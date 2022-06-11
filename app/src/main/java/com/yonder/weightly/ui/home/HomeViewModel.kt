package com.yonder.weightly.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.data.repository.WeightRepository
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.Constants
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
        getWeightHistories()
        fetchInsights()
    }

    private fun fetchInsights() = viewModelScope.launch(Dispatchers.IO) {
        val averageWeight = weightDao.getAverage()
        val maxWeight = weightDao.getMax()
        val minWeight = weightDao.getMin()
        val goalWeight = "${Hawk.get(Constants.Prefs.KEY_GOAL_WEIGHT, 0.0)}"
        _uiState.update {
            it.copy(
                goalWeight = goalWeight,
                averageWeight = "$averageWeight",
                minWeight = "$minWeight",
                maxWeight = "$maxWeight"
            )
        }
    }

    private fun getWeightHistories() = viewModelScope.launch(Dispatchers.IO) {
        weightRepository.invoke().collectLatest { weightHistories ->
            _uiState.update {
                it.copy(
                    histories = weightHistories,
                    startWeight = "${weightHistories.firstOrNull()?.formattedValue}",
                    currentWeight = "${weightHistories.lastOrNull()?.formattedValue}",
                    reversedHistories = weightHistories.take(WEIGHT_LIMIT_FOR_HOME).asReversed(),
                    shouldShowAllWeightButton = weightHistories.size > WEIGHT_LIMIT_FOR_HOME,
                    barEntries = weightHistories.mapIndexed { index, weight ->
                        BarEntry(index.toFloat(), weight?.value.orZero())
                    },
                    shouldShowEmptyView = weightHistories.isEmpty()
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
        var shouldShowAllWeightButton: Boolean = false
    )

    companion object{
        const val WEIGHT_LIMIT_FOR_HOME = 10
    }
}