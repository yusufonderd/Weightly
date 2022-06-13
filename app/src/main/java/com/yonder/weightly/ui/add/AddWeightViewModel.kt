package com.yonder.weightly.ui.add

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonder.weightly.R
import com.yonder.weightly.data.local.WeightDao
import com.yonder.weightly.domain.mapper.WeightEntityMapper
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.domain.usecase.DeleteWeight
import com.yonder.weightly.domain.usecase.SaveOrUpdateWeight
import com.yonder.weightly.utils.extensions.endOfDay
import com.yonder.weightly.utils.extensions.startOfDay
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddWeightViewModel @Inject constructor(
    private val weightDao: WeightDao,
    private val saveOrUpdateWeight: SaveOrUpdateWeight,
    private val deleteWeight: DeleteWeight
) : ViewModel() {

    sealed class Event {
        object PopBackStack : Event()
        data class ShowToast(@StringRes val textResId: Int) : Event()
    }


    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState


    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun delete(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            deleteWeight.invoke(date)
        }
    }

    fun saveOrUpdateWeight(weight: String, note: String, emoji: String, date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            when {
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
                    eventChannel.send(Event.PopBackStack)
                }
            }
        }
    }

    fun fetchDate(date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            val weightList = weightDao.fetchBy(
                startDate = date.startOfDay(),
                endDate = date.endOfDay()
            )
            val uiModel = WeightEntityMapper.map(weightList.firstOrNull())
            _uiState.update {
                it.copy(currentWeight = uiModel)
            }
        }
    }

    data class UiState(
        var currentWeight: WeightUIModel? = null
    )
}