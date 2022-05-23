package com.yonder.weightly.ui.home.chart

import com.github.mikephil.charting.formatter.ValueFormatter
import com.yonder.weightly.domain.uimodel.WeightUIModel

class XAxisValueDateFormatter(var histories: List<WeightUIModel?>) : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        val history: WeightUIModel? = histories[value.toInt()]
        return history?.formattedDate.orEmpty()
    }
}