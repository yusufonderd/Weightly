package com.yonder.weightly.utils.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.StringRes

fun Context?.showToast(@StringRes textResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, textResId, duration).show()
}