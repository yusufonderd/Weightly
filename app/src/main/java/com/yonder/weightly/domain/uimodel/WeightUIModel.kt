package com.yonder.weightly.domain.uimodel

import android.os.Parcelable
import androidx.annotation.ColorRes
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class WeightUIModel(
    var uid: Int,
    var value: Float,
    var valueWithUnit: String,
    var valueText: String,
    var emoji: String,
    var note: String,
    var date: Date,
    var formattedDate: String,
    var formattedValue: String,
    var difference: String,
    @ColorRes var differenceColor: Int
) : Parcelable

@Parcelize
data class WeightDateModel(
    var selectedDate: Date
): Parcelable