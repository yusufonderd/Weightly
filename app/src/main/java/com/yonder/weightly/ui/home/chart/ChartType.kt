package com.yonder.weightly.ui.home.chart

enum class ChartType(var value: Int) {
    LINE(0),
    BAR(1);
    companion object{
        fun valueOf(value: Int): ChartType = values().find { it.value == value } ?: LINE
    }
}