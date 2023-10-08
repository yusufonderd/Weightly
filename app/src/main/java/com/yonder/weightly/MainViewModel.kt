package com.yonder.weightly

import androidx.lifecycle.ViewModel
import com.yonder.weightly.domain.usecase.SendFeedbackToFirebase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val sendFeedbackToFirebase: SendFeedbackToFirebase
) : ViewModel() {

    fun addFeedback(text: String?) {
        if (text == null) return
        sendFeedbackToFirebase(text)
    }

}