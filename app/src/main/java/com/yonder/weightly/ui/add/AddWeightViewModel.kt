package com.yonder.weightly.ui.add

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonder.weightly.BuildConfig
import com.yonder.weightly.R
import com.yonder.weightly.billing.BillingHelper
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.domain.mapper.WeightEntityMapper
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.domain.usecase.DeleteWeight
import com.yonder.weightly.domain.usecase.SaveOrUpdateWeight
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.coroutines.CoroutineDispatchers
import com.yonder.weightly.utils.extensions.EMPTY
import com.yonder.weightly.utils.extensions.ZERO
import com.yonder.weightly.utils.extensions.endOfDay
import com.yonder.weightly.utils.extensions.startOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddWeightViewModel @Inject constructor(
    private val weightDao: WeightDao,
    private val saveOrUpdateWeight: SaveOrUpdateWeight,
    private val deleteWeight: DeleteWeight,
    private val mapper: WeightEntityMapper,
    private val billingHelper: BillingHelper,
    private val dispatcher: CoroutineDispatchers
) : ViewModel() {
    sealed class Event {
        data object ShowInterstitialAd : Event()
        data object PopBackStack : Event()
        data class ShowToast(@StringRes val textResId: Int) : Event()
    }

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    private var billingJob: Job? = null

    fun checkIsPremiumUser() {
        billingJob = viewModelScope.launch {
            billingHelper.isPurchased(Constants.PREMIUM_ACCOUNT).collectLatest { isPremium ->
                _uiState.update {
                    it.copy(shouldShowAds = isPremium.not())
                }
            }
        }
    }

    fun cancelJobs() {
        billingJob?.cancel()
    }

    fun delete(date: Date) {
        viewModelScope.launch(dispatcher.io) {
            deleteWeight(date)
        }
    }

    fun saveOrUpdateWeight(weight: String, note: String, emoji: String, date: Date) {
        viewModelScope.launch(dispatcher.io) {
            when {
                (weight.toFloatOrNull() == Float.ZERO) -> {
                    eventChannel.send(Event.ShowToast(R.string.alert_weight_bigger_than_zero))
                }

                weight.isBlank() -> {
                    eventChannel.send(Event.ShowToast(R.string.alert_blank_weight))
                }

                else -> {
                    saveOrUpdateWeight.invoke(
                        weight = weight,
                        note = note,
                        emoji = emoji,
                        date = date
                    )
                    val shouldShowAds = BuildConfig.DEBUG.not() && uiState.value.shouldShowAds
                    if (shouldShowAds) {
                        eventChannel.send(Event.ShowInterstitialAd)
                    } else {
                        eventChannel.send(Event.PopBackStack)
                    }
                }
            }
        }
    }

    fun fetchDate(date: Date) {
        viewModelScope.launch(dispatcher.io) {
            val weightList = weightDao.fetchBy(
                startDate = date.startOfDay(),
                endDate = date.endOfDay()
            )
            var uiModel: WeightUIModel? = mapper.map(weightList.firstOrNull())
            if (uiModel == null) {
                uiModel = mapper.map(
                    weightDao.fetchLastWeight().firstOrNull()
                )?.copy(emoji = String.EMPTY, note = String.EMPTY)
                _uiState.update {
                    it.copy(
                        currentWeight = uiModel,
                        shouldShowSaveButton = true
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        currentWeight = uiModel,
                        shouldShowSaveButton = false
                    )
                }

            }
        }
    }

    data class UiState(
        var currentWeight: WeightUIModel? = null,
        var shouldShowSaveButton: Boolean = true,
        var shouldShowAds: Boolean = true
    )
}