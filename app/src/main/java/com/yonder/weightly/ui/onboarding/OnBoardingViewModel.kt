package com.yonder.weightly.ui.onboarding

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.domain.usecase.SaveOrUpdateWeight
import com.yonder.weightly.utils.Constants
import com.yonder.weightly.utils.enums.MeasureUnit
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

    private fun sendEvent(event: Event){
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    fun save(currentWeight: Float, goalWeight: Float,unit: MeasureUnit) {
        if (currentWeight == goalWeight) {
            sendEvent(Event.Message(R.string.alert_current_weight_must_different_with_goal_weight))
            return
        }
        if (currentWeight.toInt() == Int.ZERO) {
            sendEvent(Event.Message(R.string.alert_weight_bigger_than_zero))
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            Hawk.put(Constants.Prefs.KEY_GOAL_WEIGHT, goalWeight)
            Hawk.put(Constants.Prefs.KEY_GOAL_WEIGHT_UNIT, MeasureUnit.findValue(unit.value).value)
            Hawk.put(Constants.Prefs.KEY_GOAL_WEIGHT_DATE, Date().time)
            Hawk.put(Constants.Prefs.KEY_SHOULD_SHOW_ON_BOARDING, false)
            saveOrUpdateWeight.invoke("$currentWeight", String.EMPTY, String.EMPTY, Date())
            eventChannel.send(Event.NavigateToHome)
        }
    }

}
