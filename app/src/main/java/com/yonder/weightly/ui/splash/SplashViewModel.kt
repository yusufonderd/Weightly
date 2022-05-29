package com.yonder.weightly.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    sealed class Event {
        object NavigateToHome : Event()
        object NavigateToOnBoardingScreen : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun startSplash() = viewModelScope.launch {
        delay(1000)
        val shouldShowOnBoardingScreen = Hawk.get(Constants.KEY_SHOULD_SHOW_ON_BOARDING, true)
        if (shouldShowOnBoardingScreen) {
            eventChannel.send(Event.NavigateToOnBoardingScreen)
        }else{
            eventChannel.send(Event.NavigateToHome)
        }
    }

}