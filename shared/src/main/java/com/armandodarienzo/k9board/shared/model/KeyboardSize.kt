package com.armandodarienzo.k9board.shared.model

enum class KeyboardSize(val value: Int) {
    VERY_SMALL(220),
    SMALL(280),
    MEDIUM(320),
    LARGE(380),
    VERY_LARGE(440);

    companion object {
        private val map = KeyboardSize.values().associateBy { it.value }
        infix fun from(value: Int) = map[value]
    }
}