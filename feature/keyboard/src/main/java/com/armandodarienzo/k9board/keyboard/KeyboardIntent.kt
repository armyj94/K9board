package com.armandodarienzo.k9board.keyboard

import com.armandodarienzo.k9board.shared.model.DoubleSpaceCharacter

sealed class KeyboardIntent {
    // User key presses — ViewModel has text context from the last lifecycle event
    data class T9KeyPressed(val digitCode: Int) : KeyboardIntent()
    data class ManualKeyPressed(val codes: IntArray, val keyId: Int) : KeyboardIntent()
    object SpacePressed : KeyboardIntent()
    object DoubleSpacePressed : KeyboardIntent()
    object DeletePressed : KeyboardIntent()
    object SwapWord : KeyboardIntent()
    data class WriteSpecificChar(val char: String) : KeyboardIntent()
    data class EmojiSelected(val emoji: String) : KeyboardIntent()
    object EnterManualMode : KeyboardIntent()
    object ExitManualMode : KeyboardIntent()
    data class ShiftToggled(val lastShiftMs: Long, val nowMs: Long) : KeyboardIntent()
    object ImeActionPressed : KeyboardIntent()
    object NewLinePressed : KeyboardIntent()

    // Service lifecycle — carry text context so ViewModel stays current
    data class InputStarted(
        val textBefore: String,
        val textAfter: String,
        val selectedText: String,
        val classInputType: Int,
        val variationInputType: Int,
        val imeActionId: Int,
    ) : KeyboardIntent()

    data class SelectionUpdated(
        val textBefore: String,
        val textAfter: String,
        val selectedText: String,
        val newSelStart: Int,
    ) : KeyboardIntent()

    data class InputFinished(
        val textBefore: String,
        val textAfter: String,
        val selectedText: String,
    ) : KeyboardIntent()

    data class WindowShown(val languageTag: String) : KeyboardIntent()

    data class PreferencesLoaded(
        val languageSet: String,
        val themeSet: String,
        val keyboardSize: Int,
        val hapticFeedback: Boolean,
        val backgroundColorId: Int,
        val isManualDefault: Boolean,
        val doubleSpaceChar: DoubleSpaceCharacter,
        val isAutoCaps: Boolean,
    ) : KeyboardIntent()
}
