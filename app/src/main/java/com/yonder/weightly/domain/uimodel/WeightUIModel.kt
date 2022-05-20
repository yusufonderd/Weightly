package com.yonder.weightly.domain.uimodel

import java.util.*

data class WeightUIModel(
    var uid : Int,
    var value: Float,
    var valueText : String,
    var emoji : String,
    var note : String,
    var date : Date
    )