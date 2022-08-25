package com.yonder.weightly.domain.decider

import com.yonder.weightly.R
import com.yonder.weightly.domain.mapper.DEFAULT_VALUE_OF_WEIGHT_DIFFERENCE
import com.yonder.weightly.utils.extensions.format
import com.yonder.weightly.utils.extensions.orZero
import javax.inject.Inject

class DifferenceDecider @Inject constructor() {
    fun provideColor(difference: Float): Int {
        return if (difference == 0.0f) {
            R.color.gray_500
        } else if (difference > 0.0f) {
            R.color.red
        } else {
            R.color.green
        }
    }

    fun provideText(difference: Float): String {
        val differenceBuilder = StringBuilder()
        if (difference > 0) {
            differenceBuilder.append("+")
        }
        differenceBuilder.append(difference.format(1))

        return differenceBuilder.toString()
    }

    fun provideValue(current: Float?, previous: Float?): Float {
        return if (previous.orZero() == 0.0f) {
            DEFAULT_VALUE_OF_WEIGHT_DIFFERENCE
        } else {
            current.orZero() - previous.orZero()
        }
    }
}