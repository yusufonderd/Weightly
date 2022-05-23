package com.yonder.weightly.ui.home.chart

import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import com.yonder.weightly.domain.uimodel.WeightUIModel
import com.yonder.weightly.utils.extensions.orZero

class WeightValueFormatter(var histories: List<WeightUIModel?>) : ValueFormatter() {
    override fun getBarLabel(barEntry: BarEntry?): String {
        val history = histories[barEntry?.x?.toInt().orZero]
        return "${history?.emoji} ${history?.valueText}"
    }

}