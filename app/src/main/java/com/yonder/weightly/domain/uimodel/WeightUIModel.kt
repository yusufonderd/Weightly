package com.yonder.weightly.domain.uimodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*

@Parcelize
data class WeightUIModel(
    var uid: Int,
    var value: Float,
    var valueText: String,
    var emoji: String,
    var note: String,
    var date: Date,
    var formattedDate : String
) : Parcelable