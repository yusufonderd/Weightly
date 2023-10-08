package com.yonder.weightly.ui.openlock

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.network.ApiService
import com.yonder.weightly.network.SendEmailRequest
import com.yonder.weightly.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject


@HiltViewModel
class OpenLockViewModel @Inject constructor(
    private val apiService: ApiService
) : ViewModel() {

    sealed class Event {
        object NavigateToHome : Event()
        data class ShowMessage(@StringRes var message: Int) : Event()
        data class Message(var message: String) : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun checkPassword(password: String) {
        viewModelScope.launch {
            if (password.isBlank()) {
                eventChannel.send(Event.ShowMessage(R.string.please_make_sure_to_fill_all_fields))
            } else {
                if (password == Hawk.get(Constants.Prefs.APP_LOCK_PASSWORD)) {
                    eventChannel.send(Event.NavigateToHome)
                } else {
                    eventChannel.send(Event.ShowMessage(R.string.wrong_password))
                }
            }
        }
    }

    fun forgotPassword() {
        viewModelScope.launch {
            try {
                val body = apiService.sendEmail(
                    SendEmailRequest(
                        appName = "weightly",
                        email = Hawk.get(Constants.Prefs.RECOVERY_EMAIL),
                        subject = "Weightly recovery e-mail",
                        content = "Your password:" + Hawk.get(Constants.Prefs.APP_LOCK_PASSWORD)
                    )
                ).body()

                if (body?.success == true){
                    eventChannel.send(Event.ShowMessage(R.string.recovery_email_sent_successfully))
                }else{
                    eventChannel.send(Event.ShowMessage(R.string.error_occurred))
                }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

}
