package com.yonder.weightly.utils.extensions

import java.text.SimpleDateFormat
import java.util.*


fun Date.toFormat(dateFormat: String): String {
    return SimpleDateFormat(dateFormat, Locale.getDefault()).format(this)
}