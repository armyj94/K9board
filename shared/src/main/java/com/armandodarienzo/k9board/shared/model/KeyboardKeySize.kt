package com.armandodarienzo.k9board.shared.model

enum class KeyboardKeySize(val value: Int) {
    SMALL(40),
    MEDIUM(50),
    LARGE(60),
    VERY_LARGE(65);

    companion object {
        private val map = KeyboardKeySize.values().associateBy { it.value }
        infix fun from(value: Int) = map[value]
    }
}