package com.armandodarienzo.k9board.keyboard

import android.text.InputType
import com.armandodarienzo.k9board.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.shared.WORDS_REGEX_STRING
import com.armandodarienzo.k9board.shared.substringAfterLastNotMatching
import com.armandodarienzo.k9board.shared.substringBeforeFirstNotMatching
import com.armandodarienzo.k9board.shared.ui.base.Reducer

data class KeyboardState(
    // UI-visible state
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

    // Internal text-tracking state
    val capsIndexes: List<Int> = emptyList(),
    val textCompositionStart: Int = 0,
    val textCompositionEnd: Int = 0,
    val textCompositionText: String = "",
    val textSelectionStart: Int = 0,
    val textSelectionEnd: Int = 0,
    val textSelectionText: String = "",
    val textBeforeCursor: String = "",
    val textAfterCursor: String = "",
    val words: List<Word> = emptyList(),
    val currentWord: Word? = null,
    val currentT9code: String = "",
    val wordsMaxLength: Int = 10,
    val classInputType: Int = 0,
    val variationInputType: Int = 0,
    val wasManual: Boolean = false,

    // Manual-mode cycling state
    val lastKeyId: Int = 0,
    val keyCodesIndex: Int = 0,
    val keyTimer: Long = 0L,
) : Reducer.ViewState {

    private val wordsRegex: Regex get() = WORDS_REGEX_STRING.toRegex()

    fun getWordTextBeforeCursor(): String = textBeforeCursor.substringAfterLastNotMatching(wordsRegex)

    fun getWordTextAfterCursor(): String = textAfterCursor.substringBeforeFirstNotMatching(wordsRegex)

    fun getCapsIndexesForCurrentWord(): List<Int> {
        val wordStart = textSelectionStart - getWordTextBeforeCursor().length
        val wordEnd = textSelectionStart + getWordTextAfterCursor().length
        return capsIndexes
            .filter { it >= wordStart && it <= wordEnd + 1 }
            .map { it - wordStart }
    }

    fun inputIsPassword(): Boolean {
        return variationInputType == InputType.TYPE_TEXT_VARIATION_PASSWORD ||
               variationInputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
               variationInputType == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    }
}