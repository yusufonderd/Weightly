package com.yonder.weightly.ui.home

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.github.mikephil.charting.data.BarEntry
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.domain.usecase.GetAllWeights
import com.yonder.weightly.domain.usecase.GetUserGoal
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.coroutines.CoroutineDispatchers
import com.yonder.weightly.utils.enums.ChartType
import com.yonder.weightly.utils.extensions.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

const val NO_VALUE = "-"

@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private var getAllWeights: GetAllWeights,
        private val weightDao: WeightDao,
        private val getUserGoal: GetUserGoal,
        private val dispatcher: CoroutineDispatchers,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(UiState())
        val uiState: StateFlow<UiState> = _uiState

        private var job: Job? = null
        private var billingJob: Job? = null
        private var insightsJob: Job? = null

        fun startBilling(activity: Activity) {
            // billingHelper.launchBillingFlow(activity, Constants.PREMIUM_ACCOUNT)
        }

        fun hasUserWeightForToday() {
            val today = Date()
            viewModelScope.launch(dispatcher.io) {
                val weights =
                    weightDao.fetchBy(
                        startDate = today.startOfDay(),
                        endDate = today.endOfDay(),
                    )
                _uiState.update {
                    it.copy(
                        shouldShowAddWeightForTodayButton = weights.isEmpty(),
                    )
                }
            }
        }

        fun checkIsPremiumUser() {
            billingJob =
                viewModelScope.launch {
                    _uiState.update {
                        it.copy(shouldShowAds = true)
                    }
                }
        }

        fun fetchInsights() {
            insightsJob =
                viewModelScope.launch(dispatcher.io) {
                    combine(weightDao.getMax(), weightDao.getMin(), weightDao.getAvg()) { max, min, avg ->
                        _uiState.update {
                            it.copy(
                                minWeight = min?.format(1)?.replaceWithDot(),
                                maxWeight = max?.format(1)?.replaceWithDot(),
                                averageWeight = avg?.format(1)?.replaceWithDot(),
                            )
                        }
                    }.stateIn(this)
                }
        }

        fun cancelJobs() {
            job?.cancel()
            billingJob?.cancel()
            insightsJob?.cancel()
        }

        fun fetchHome() {
            job =
                viewModelScope.launch(dispatcher.io) {
                    getAllWeights().collectLatest { weightHistories ->
                        if (weightHistories.size > 1) {
                            fetchInsights()
                        } else {
                            _uiState.update {
                                it.copy(
                                    minWeight = NO_VALUE,
                                    maxWeight = NO_VALUE,
                                    averageWeight = NO_VALUE,
                                )
                            }
                        }
                        _uiState.update {
                            it.copy(
                                histories = weightHistories,
                                startWeight = "${weightHistories.firstOrNull()?.formattedValue}",
                                currentWeight = "${weightHistories.lastOrNull()?.formattedValue}",
                                barEntries =
                                    weightHistories.mapIndexed { index, weight ->
                                        BarEntry(index.toFloat(), weight?.value.orZero())
                                    },
                                userGoal = getUserGoal(),
                                shouldShowLimitLine = Hawk.get(Constants.Prefs.KEY_CHART_LIMIT_LINE, false),
                                chartType =
                                    ChartType.findValue(
                                        Hawk.get(
                                            Constants.Prefs.KEY_CHART_TYPE,
                                            0,
                                        ),
                                    ),
                                shouldShowEmptyView = weightHistories.isEmpty(),
                                goalWeight = "${Hawk.get(Constants.Prefs.KEY_GOAL_WEIGHT, 0.0)}",
                            )
                        }
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
            var shouldShowAddWeightForTodayButton: Boolean = false,
            var shouldShowLimitLine: Boolean = false,
            var chartType: ChartType = ChartType.LINE,
            var userGoal: String? = null,
            var shouldShowAds: Boolean = true,
        )
    }
