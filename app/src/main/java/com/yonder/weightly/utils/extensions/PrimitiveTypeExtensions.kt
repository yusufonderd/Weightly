package com.yonder.weightly.utils.extensions

import android.content.Context
import android.util.DisplayMetrics

fun Float?.orZero() = this ?: 0.0f

val Int?.orZero: Int get() = this ?: 0

val Int.Companion.ZERO: Int get() = 0

val Float.Companion.ZERO: Float get() = 0.0f

val String.Companion.EMPTY: String get() = ""

fun String.replaceWithDot(): String{
   return  replace(",",".")
}

fun Int.dpToPx(context: Context): Int {
    val displayMetrics = context.resources.displayMetrics
    return this * (displayMetrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT)
}

fun Float.format(digits: Int) = "%.${digits}f".format(this)
