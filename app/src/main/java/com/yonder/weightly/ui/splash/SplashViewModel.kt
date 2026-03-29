package com.yonder.weightly.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yonder.weightly.data.local.PreferenceManager
import com.yonder.weightly.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val preferenceManager: PreferenceManager
) : ViewModel() {
    sealed class Event {
        data object NavigateToHome : Event()
        data object NavigateToOpenLockScreen : Event()
        data object NavigateToOnBoardingScreen : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun startSplash() = viewModelScope.launch {
        delay(1000)
        val shouldShowOnBoardingScreen = preferenceManager.get(Constants.Prefs.KEY_SHOULD_SHOW_ON_BOARDING, true)
        if (shouldShowOnBoardingScreen) {
            eventChannel.send(Event.NavigateToOnBoardingScreen)
        } else if (preferenceManager.get(Constants.Prefs.IS_APP_LOCKED, false)) {
            eventChannel.send(Event.NavigateToOpenLockScreen)
        } else {
            eventChannel.send(Event.NavigateToHome)
        }
    }
}