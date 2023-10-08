package com.yonder.weightly.utils.enums


enum class ThemeType(var value: String) {
    DEFAULT("0"),
    LIGHT("1"),
    DARK("2");

    companion object {
        fun findValue(value: String): ThemeType = values().find { it.value == value } ?: DEFAULT
    }
}
