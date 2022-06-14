package com.yonder.weightly.ui.onboarding

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.domain.usecase.SaveOrUpdateWeight
import com.yonder.weightly.uicomponents.MeasureUnit
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.extensions.EMPTY
import com.yonder.weightly.utils.extensions.ZERO
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val saveOrUpdateWeight: SaveOrUpdateWeight
) : ViewModel() {

    sealed class Event {
        object NavigateToHome : Event()
        data class Message(@StringRes var message: Int) : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun save(currentWeight: Float, goalWeight: Float,unit: MeasureUnit) {
        if (currentWeight == goalWeight) {
            viewModelScope.launch {
                eventChannel.send(Event.Message(R.string.alert_current_weight_must_different_with_goal_weight))
            }
            return
        }
        if (currentWeight.toInt() == Int.ZERO) {
            viewModelScope.launch {
                eventChannel.send(Event.Message(R.string.alert_weight_bigger_than_zero))
            }
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            Hawk.put(Constants.Prefs.KEY_GOAL_WEIGHT, goalWeight)
            Hawk.put(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT, unit.name)
            Hawk.put(Constants.Prefs.KEY_GOAL_WEIGHT_DATE, Date().time)
            Hawk.put(Constants.Prefs.KEY_SHOULD_SHOW_ON_BOARDING, false)
            saveOrUpdateWeight.invoke("$currentWeight", String.EMPTY, String.EMPTY, Date())
            eventChannel.send(Event.NavigateToHome)
        }
    }

}
