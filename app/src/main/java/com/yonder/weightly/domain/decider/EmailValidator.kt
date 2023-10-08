package com.yonder.weightly.domain.decider

import androidx.core.util.PatternsCompat
import javax.inject.Inject

class EmailValidator @Inject constructor() {

    fun isValidEmail(email: String): Boolean {
        return email.isNotEmpty() && PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()
    }

}