package com.yonder.weightly.ui.add

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonder.weightly.R
import com.yonder.weightly.data.local.AppDatabase
import com.yonder.weightly.data.local.WeightEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class AddWeightViewModel @Inject constructor(
    private val appDatabase: AppDatabase
) : ViewModel() {

    sealed class Event {
        object PopBackStack: Event()
        data class ShowToast(@StringRes val textResId: Int): Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun addWeight(weight: String, note: String, date: Date) {
        viewModelScope.launch(Dispatchers.IO) {
            when{
                weight.isBlank() -> {
                    eventChannel.send(Event.ShowToast(R.string.alert_blank_weight))
                }
                else -> {
                    appDatabase.weightDao()
                        .insert(WeightEntity(
                            timestamp = date,
                            value = weight.toFloat(),
                            emoji = "E",
                            note = note
                        ))
                    eventChannel.send(Event.PopBackStack)
                }
            }
        }
    }

}