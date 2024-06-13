package com.armandodarienzo.k9board.shared.model

import com.armandodarienzo.k9board.shared.R

enum class KeyboardSize(override val value: Double) : PreferencesOption<Double>  {
    VERY_SMALL(0.2) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_very_small
        }
    },
    SMALL(0.3) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_small
        }
    },
    MEDIUM(0.35) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_medium
        }
    },
    LARGE(0.4) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_large
        }
    },
    VERY_LARGE(0.5) {
        override fun getLabelId(): Int {
            return R.string.keyboard_size_very_large
        }
    };

    //To improve code readability
    val factor : Double get() = value

    companion object {
        private val map = KeyboardSize.values().associateBy { it.value }
        infix fun from(value: Double) = map[value]
    }
}