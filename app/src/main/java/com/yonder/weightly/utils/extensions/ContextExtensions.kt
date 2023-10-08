package com.yonder.weightly.utils.extensions

import android.content.Context
import android.widget.Toast
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.core.content.ContextCompat

fun Context?.showToast(@StringRes textResId: Int, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, textResId, duration).show()
}
fun Context?.showToast( text: String, duration: Int = Toast.LENGTH_SHORT) {
    Toast.makeText(this, text, duration).show()
}
internal fun Context.getColorCompat(@ColorRes color: Int) = ContextCompat.getColor(this, color)
