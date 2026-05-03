package com.armandodarienzo.k9board.keyboard

import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.shared.model.DoubleSpaceCharacter

data class KeyboardState(
    val capsStatus: KeyboardCapsStatus = KeyboardCapsStatus.UPPER_CASE,
    val isManual: Boolean = false,
    val isAutoCaps: Boolean = false,
    val doubleSpaceChar: DoubleSpaceCharacter = DoubleSpaceCharacter.NONE,
    val imeActionId: Int? = null,
    val backgroundColorId: Int = 0,
    val languageSet: String = "",
    val keyboardSize: Int = 280,
    val hapticFeedback: Boolean = false,
    val themeSet: String = "",
)
