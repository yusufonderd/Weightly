package com.yonder.weightly.utils


import android.os.SystemClock
import android.view.View

class SafeClickListenerExtension(
    private var defaultInterval: Int = 500,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < defaultInterval) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListenerExtension {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}
