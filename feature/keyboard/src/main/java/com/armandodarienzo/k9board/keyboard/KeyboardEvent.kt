package com.armandodarienzo.k9board.keyboard

import com.armandodarienzo.k9board.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.shared.ui.base.Reducer

sealed class KeyboardEvent : Reducer.ViewEvent {
    // Result of async T9 word-suggestion lookup
    data class T9WordsReady(
        val suggestions: List<Word>,
        val newCode: String,
        val digitChar: Char,
    ) : KeyboardEvent()

    // A word was swapped in T9 mode
    data class WordSwapped(val newWord: Word) : KeyboardEvent()

    // Manual key press — ViewModel pre-computes isNewWord and currentTimeMs to keep the Reducer pure
    data class ManualCharAdded(
        val charCode: Int,
        val isNewWord: Boolean,
        val keyId: Int,
        val currentTimeMs: Long,
    ) : KeyboardEvent()

    // Simple text commit (space, double-space, emoji, newline, writeSpecificChar)
    data class TextCommitted(
        val text: String,
        val lowerCaseAfterCommit: Boolean = false,
    ) : KeyboardEvent()

    // Backspace / delete
    data object DeleteKeyPressed : KeyboardEvent()

    // Result of async composing-region word lookup
    data class ComposingRegionReady(
        val composingStart: Int,
        val composingEnd: Int,
        val currentWord: Word?,
    ) : KeyboardEvent()

    // Caps/shift state changed
    data class CapsStatusUpdated(val newStatus: KeyboardCapsStatus) : KeyboardEvent()

    // Mode transitions
    data object ManualModeEntered : KeyboardEvent()
    data object ManualModeExited : KeyboardEvent()

    // IME action — effect only, no state change
    data class ImeActionTriggered(val actionId: Int) : KeyboardEvent()

    // Input connection lifecycle
    data class InputConnectionStarted(
        val textBefore: String,
        val textAfter: String,
        val selectedText: String,
        val classInputType: Int,
        val variationInputType: Int,
        val imeActionId: Int,
    ) : KeyboardEvent()

    data class SelectionMoved(
        val textBefore: String,
        val textAfter: String,
        val selectedText: String,
        val newSelStart: Int,
    ) : KeyboardEvent()

    data object InputConnectionFinished : KeyboardEvent()

    // After async repo initialisation + maxLength query
    data class PreferencesApplied(
        val languageSet: String,
        val themeSet: String,
        val keyboardSize: Int,
        val hapticFeedback: Boolean,
        val backgroundColorId: Int,
        val isManualDefault: Boolean,
        val doubleSpaceChar: DoubleSpaceCharacter,
        val isAutoCaps: Boolean,
        val wordsMaxLength: Int,
    ) : KeyboardEvent()

    // Auto-caps check result
    data class AutoCapsApplied(val newStatus: KeyboardCapsStatus) : KeyboardEvent()
}