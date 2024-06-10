package com.armandodarienzo.k9board.shared.model

enum class DoubleSpaceCharacter(val value: String) {
    NONE(""),
    DOT("."),
    COMMA(",");

    companion object {
        private val map = DoubleSpaceCharacter.values().associateBy { it.value }
        infix fun from(value: String) = map[value]
    }

}