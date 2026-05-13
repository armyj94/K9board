package com.armandodarienzo.k9board.keyboard

import com.armandodarienzo.k9board.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.shared.ui.base.MviProcessor

sealed class KeyboardAction : MviProcessor.MviAction {
    // User key presses
    data class T9KeyPressed(val digitCode: Int) : KeyboardAction()
    data class ManualKeyPressed(val codes: IntArray, val keyId: Int) : KeyboardAction()
    data object SpacePressed : KeyboardAction()
    data object DoubleSpacePressed : KeyboardAction()
    data object DeletePressed : KeyboardAction()
    data object SwapWord : KeyboardAction()
    data class WriteSpecificChar(val char: String) : KeyboardAction()
    data class EmojiSelected(val emoji: String) : KeyboardAction()
    data object EnterManualMode : KeyboardAction()
    data object ExitManualMode : KeyboardAction()
    data class ShiftToggled(val lastShiftMs: Long, val nowMs: Long) : KeyboardAction()
    data object ImeActionPressed : KeyboardAction()
    data object NewLinePressed : KeyboardAction()

    // Service lifecycle
    data class InputStarted(
        val textBefore: String,
        val textAfter: String,
        val selectedText: String,
        val classInputType: Int,
        val variationInputType: Int,
        val imeActionId: Int,
    ) : KeyboardAction()

    data class SelectionUpdated(
        val textBefore: String,
        val textAfter: String,
        val selectedText: String,
        val newSelStart: Int,
    ) : KeyboardAction()

    data class InputFinished(
        val textBefore: String,
        val textAfter: String,
        val selectedText: String,
    ) : KeyboardAction()

    data class WindowShown(val languageTag: String) : KeyboardAction()

    data class PreferencesLoaded(
        val languageSet: String,
        val themeSet: String,
        val keyboardSize: Int,
        val hapticFeedback: Boolean,
        val backgroundColorId: Int,
        val isManualDefault: Boolean,
        val doubleSpaceChar: DoubleSpaceCharacter,
        val isAutoCaps: Boolean,
    ) : KeyboardAction()
}