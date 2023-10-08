package com.yonder.weightly.utils.extensions

import android.widget.TextView
import androidx.annotation.ColorRes

internal fun TextView.setTextColorRes(@ColorRes color: Int) = setTextColor(context.getColorCompat(color))

