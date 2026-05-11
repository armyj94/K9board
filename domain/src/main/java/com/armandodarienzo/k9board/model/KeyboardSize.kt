package com.armandodarienzo.k9board.model

enum class KeyboardSize(val factor: Double) {
    VERY_SMALL(0.2),
    SMALL(0.3),
    MEDIUM(0.35),
    LARGE(0.4),
    VERY_LARGE(0.5);

    companion object {
        private val map = entries.associateBy { it.factor }
        infix fun from(value: Double) = map[value]
    }
}