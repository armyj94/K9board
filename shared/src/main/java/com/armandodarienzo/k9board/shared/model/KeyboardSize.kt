package com.armandodarienzo.k9board.shared.model

import com.armandodarienzo.k9board.shared.R

enum class KeyboardSize(override val value: Int) : PreferencesOption<Int>  {
    VERY_SMALL(220) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_very_small
        }
    },
    SMALL(280) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_small
        }
    },
    MEDIUM(320) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_medium
        }
    },
    LARGE(380) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_large
        }
    },
    VERY_LARGE(440) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_very_large
        }
    };

    companion object {
        private val map = KeyboardSize.values().associateBy { it.value }
        infix fun from(value: Int) = map[value]
    }
}