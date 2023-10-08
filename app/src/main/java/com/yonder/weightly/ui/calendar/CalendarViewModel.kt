package com.yonder.weightly.ui.calendar

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonder.weightly.billing.BillingHelper
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.domain.mapper.WeightEntityMapper
import com.yonder.weightly.domain.uimodel.WeightDateModel
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.domain.usecase.GetAllWeights
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.extensions.endOfDay
import com.yonder.weightly.utils.extensions.startOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject


@HiltViewModel
class CalendarViewModel @Inject constructor(
    private val getAllWeights: GetAllWeights,
    private val billingHelper: BillingHelper,
    private val weightDao: WeightDao,
    private val mapper: WeightEntityMapper
) :
    ViewModel() {

    sealed class Event {
        data class NavigateToWeight(var model: WeightUIModel) : Event()
        data class NavigateToNewWeight(var model: WeightDateModel) : Event()
    }

    var selectedLocalDate = LocalDate.now()

    val today = LocalDate.now()

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    var job: Job? = null
    var billingJob: Job? = null

    fun checkIsPremiumUser() {
        billingJob = viewModelScope.launch {
            billingHelper.isPurchased(Constants.PREMIUM_ACCOUNT).collectLatest { isPremium ->
                _uiState.update {
                    it.copy(
                        shouldShowAds = isPremium.not()
                    )
                }
            }
        }
    }

    fun getWeightByDate(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            val weightList = weightDao.fetchBy(
                startDate = date.startOfDay(),
                endDate = date.endOfDay()
            )
            val weightEntity = weightList.firstOrNull()
            if (weightEntity != null){
                eventChannel.send(Event.NavigateToWeight(mapper.map(weightEntity)!!))
            }else{
                eventChannel.send(Event.NavigateToNewWeight(WeightDateModel(date)))
            }
        }
    }


    fun getWeightHistories() {
        job = viewModelScope.launch(Dispatchers.IO) {
            getAllWeights().collectLatest { weightHistories ->
                _uiState.update {
                    it.copy(
                        histories = weightHistories.reversed()
                    )
                }
            }
        }
    }

    fun cancelJobs() {
        job?.cancel()
        billingJob?.cancel()
    }

    data class UiState(
        var histories: List<WeightUIModel?> = emptyList(),
        var shouldShowAds: Boolean = true
    )

}
