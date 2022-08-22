package com.yonder.weightly.utils.enums

enum class MeasureUnit(var value: String) {
    KG("kg"),
    LB("lb");

    companion object {
        fun findValue(value: String?): MeasureUnit = values().find { it.value == value } ?: KG
    }
}