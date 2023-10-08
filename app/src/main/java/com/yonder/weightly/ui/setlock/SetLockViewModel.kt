package com.yonder.weightly.ui.setlock

import androidx.annotation.StringRes
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.orhanobut.hawk.Hawk
import com.yonder.weightly.R
import com.yonder.weightly.domain.decider.EmailValidator
import com.yonder.weightly.utils.Constants
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SetLockViewModel @Inject constructor(
    private val emailValidator: EmailValidator
) :
    ViewModel() {

    sealed class Event {
        object NavigateToSplash : Event()
        data class ShowMessage(@StringRes var message: Int) : Event()
    }

    private val eventChannel = Channel<Event>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun setLock(
        password: String,
        passwordAgain: String,
        passwordHint: String,
        recoveryEmail: String
    ) {

        val inputFields = listOf(password, passwordAgain, passwordHint, recoveryEmail)
        viewModelScope.launch {
            if (inputFields.any { it.isBlank() }) {
                eventChannel.send(Event.ShowMessage(R.string.please_make_sure_to_fill_all_fields))
            } else if (emailValidator.isValidEmail(recoveryEmail).not()) {
                eventChannel.send(Event.ShowMessage(R.string.invalid_email_address))
            } else {
                if (password != passwordAgain) {
                    eventChannel.send(Event.ShowMessage(R.string.password_does_not_match))
                } else {
                    Hawk.put(Constants.Prefs.APP_LOCK_HINT, passwordHint)
                    Hawk.put(Constants.Prefs.APP_LOCK_PASSWORD, password)
                    Hawk.put(Constants.Prefs.RECOVERY_EMAIL, recoveryEmail)
                    Hawk.put(Constants.Prefs.IS_APP_LOCKED, true)
                    eventChannel.send(Event.NavigateToSplash)
                }
            }
        }
    }

}