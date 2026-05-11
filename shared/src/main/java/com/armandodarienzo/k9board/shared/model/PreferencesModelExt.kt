package com.armandodarienzo.k9board.shared.model

import com.armandodarienzo.k9board.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.model.KeyboardSize
import com.armandodarienzo.k9board.shared.R

fun KeyboardSize.getLabelId(): Int = when (this) {
    KeyboardSize.VERY_SMALL -> R.string.keyboard_size_very_small
    KeyboardSize.SMALL -> R.string.keyboard_size_small
    KeyboardSize.MEDIUM -> R.string.keyboard_size_medium
    KeyboardSize.LARGE -> R.string.keyboard_size_large
    KeyboardSize.VERY_LARGE -> R.string.keyboard_size_very_large
}

fun DoubleSpaceCharacter.getLabelId(): Int = when (this) {
    DoubleSpaceCharacter.NONE -> R.string.double_space_none
    DoubleSpaceCharacter.DOT -> R.string.double_space_dot
    DoubleSpaceCharacter.COMMA -> R.string.double_space_comma
}
