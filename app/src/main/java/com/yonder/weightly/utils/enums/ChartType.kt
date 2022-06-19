package com.yonder.weightly.utils.enums

enum class ChartType(var value: Int) {
    LINE(0),
    BAR(1);

    companion object {
        fun findValue(value: Int): ChartType = values().find { it.value == value } ?: LINE
    }
}