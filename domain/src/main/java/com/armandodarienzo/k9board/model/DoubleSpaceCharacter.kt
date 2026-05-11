package com.armandodarienzo.k9board.model

enum class DoubleSpaceCharacter(val value: String) {
    NONE(""),
    DOT("."),
    COMMA(",");

    companion object {
        private val map = values().associateBy { it.value }
        infix fun from(value: String) = map[value]
    }
}