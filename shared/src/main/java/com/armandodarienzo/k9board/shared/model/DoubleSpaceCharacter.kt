package com.armandodarienzo.k9board.shared.model

import com.armandodarienzo.k9board.shared.R

enum class DoubleSpaceCharacter(override val value: String) : PreferencesOption<String> {
    NONE("") {
        override fun getLabelId(): Int {
            return R.string.double_space_none
        }
    },
    DOT(".") {
        override fun getLabelId(): Int {
            return R.string.double_space_dot
        }
    },
    COMMA(",") {
        override fun getLabelId(): Int {
            return R.string.double_space_comma
        }
    };

    companion object {
        private val map = DoubleSpaceCharacter.values().associateBy { it.value }
        infix fun from(value: String) = map[value]
    }

}