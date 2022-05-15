package com.yonder.weightly.domain.uimodel

import java.util.*

data class WeightUIModel(
    var uid : Int,
    var value: Float,
    var emoji : String,
    var note : String,
    var date : Date
)