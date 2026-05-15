package com.armandodarienzo.k9board.keyboard

import android.view.KeyEvent
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.shared.ui.base.Reducer

class KeyboardReducer : Reducer<KeyboardState, KeyboardEvent, KeyboardEffect> {

    override fun reduce(
        previousState: KeyboardState,
        event: KeyboardEvent,
    ): Pair<KeyboardState, KeyboardEffect?> = when (event) {

        is KeyboardEvent.T9WordsReady -> {
            val wBefore = previousState.getWordTextBeforeCursor()
            val currentWord = if (event.suggestions.isNotEmpty()) event.suggestions.first()
                              else com.armandodarienzo.k9board.model.Word(
                                  wBefore + event.digitChar + previousState.getWordTextAfterCursor()
                              )

            val charArray = currentWord.text.toCharArray()
            val capsStatus = previousState.capsStatus
            val newCapsIndexes: List<Int>
            val newCaps: KeyboardCapsStatus

            if (capsStatus == KeyboardCapsStatus.UPPER_CASE || capsStatus == KeyboardCapsStatus.CAPS_LOCK) {
                newCapsIndexes = previousState.capsIndexes + previousState.textSelectionStart
                charArray[wBefore.length] = charArray[wBefore.length].uppercaseChar()
                newCaps = if (capsStatus == KeyboardCapsStatus.UPPER_CASE) KeyboardCapsStatus.LOWER_CASE
                          else capsStatus
            } else {
                newCapsIndexes = previousState.capsIndexes
                newCaps = capsStatus
            }

            val capsForWord = previousState.copy(
                capsIndexes = newCapsIndexes,
                words = event.suggestions,
                currentWord = currentWord,
                currentT9code = event.newCode,
            ).getCapsIndexesForCurrentWord()
            capsForWord.forEach { idx -> charArray[idx] = charArray[idx].uppercaseChar() }

            previousState.copy(
                words = event.suggestions,
                currentWord = currentWord,
                currentT9code = event.newCode,
                capsIndexes = newCapsIndexes,
                capsStatus = newCaps,
                textCompositionText = String(charArray),
            ) to KeyboardEffect.SetComposingText(String(charArray))
        }

        is KeyboardEvent.WordSwapped -> {
            val charArray = event.newWord.text.toCharArray()
            val capsForWord = previousState.copy(currentWord = event.newWord).getCapsIndexesForCurrentWord()
            capsForWord.forEach { idx -> charArray[idx] = charArray[idx].uppercaseChar() }
            previousState.copy(
                currentWord = event.newWord,
                textCompositionText = String(charArray),
            ) to KeyboardEffect.SetComposingText(String(charArray))
        }

        is KeyboardEvent.ManualCharAdded -> {
            val charBytes = String(intArrayOf(event.charCode), 0, 1).toByteArray(Charsets.UTF_16)
            val newText = String(String(charBytes, Charsets.UTF_16).toCharArray())

            val isContinuation = !event.isNewWord
            val newIndex = if (isContinuation && previousState.keyCodesIndex < Int.MAX_VALUE)
                previousState.keyCodesIndex + 1
            else 0

            val newCapsIndexes = if (!isContinuation && previousState.capsStatus != KeyboardCapsStatus.LOWER_CASE)
                previousState.capsIndexes + previousState.textSelectionStart
            else previousState.capsIndexes

            val effect = if (isContinuation) KeyboardEffect.SetComposingText(newText)
                         else KeyboardEffect.FinishComposingAndStart(newText)

            previousState.copy(
                capsIndexes = newCapsIndexes,
                lastKeyId = event.keyId,
                keyCodesIndex = if (isContinuation) newIndex else 0,
                keyTimer = event.currentTimeMs,
                textCompositionText = newText,
            ) to effect
        }

        is KeyboardEvent.TextCommitted -> {
            val newCaps = if (event.lowerCaseAfterCommit && previousState.capsStatus == KeyboardCapsStatus.UPPER_CASE)
                KeyboardCapsStatus.LOWER_CASE else previousState.capsStatus
            val newCapsIndexes = if (previousState.capsStatus != KeyboardCapsStatus.LOWER_CASE)
                previousState.capsIndexes + previousState.textSelectionStart
            else previousState.capsIndexes
            previousState.copy(
                capsStatus = newCaps,
                capsIndexes = newCapsIndexes,
                textCompositionStart = previousState.textBeforeCursor.length + event.text.length,
                textCompositionEnd = previousState.textBeforeCursor.length + event.text.length,
                textCompositionText = "",
            ) to KeyboardEffect.CommitText(event.text)
        }

        KeyboardEvent.DeleteKeyPressed -> {
            val newCapsIndexes = when {
                previousState.textSelectionText.isEmpty() && previousState.textBeforeCursor.isNotEmpty() ->
                    previousState.capsIndexes - (previousState.textSelectionStart - 1)
                previousState.textSelectionText.isNotEmpty() ->
                    previousState.capsIndexes.filter {
                        it !in previousState.textSelectionStart until previousState.textSelectionEnd
                    }
                else -> previousState.capsIndexes
            }
            previousState.copy(capsIndexes = newCapsIndexes) to
                KeyboardEffect.SendKeyEvent(KeyEvent.KEYCODE_DEL)
        }

        is KeyboardEvent.ComposingRegionReady -> {
            previousState.copy(
                textCompositionStart = event.composingStart,
                textCompositionEnd = event.composingEnd,
                textCompositionText = previousState.getWordTextBeforeCursor() + previousState.getWordTextAfterCursor(),
                currentWord = event.currentWord,
            ) to KeyboardEffect.SetComposingRegion(event.composingStart, event.composingEnd)
        }

        is KeyboardEvent.CapsStatusUpdated -> previousState.copy(capsStatus = event.newStatus) to null

        is KeyboardEvent.AutoCapsApplied -> previousState.copy(capsStatus = event.newStatus) to null

        KeyboardEvent.ManualModeEntered -> {
            val newCaps = if (previousState.capsStatus == KeyboardCapsStatus.UPPER_CASE)
                KeyboardCapsStatus.CAPS_LOCK else previousState.capsStatus
            previousState.copy(
                capsStatus = newCaps,
                wasManual = previousState.isManual,
                isManual = true,
                textCompositionStart = previousState.textBeforeCursor.length,
                textCompositionEnd = previousState.textBeforeCursor.length,
                textCompositionText = "",
            ) to KeyboardEffect.FinishComposing
        }

        KeyboardEvent.ManualModeExited -> {
            previousState.copy(
                isManual = previousState.wasManual,
                wasManual = false,
            ) to null
        }

        is KeyboardEvent.ImeActionTriggered ->
            previousState to KeyboardEffect.PerformEditorAction(event.actionId)

        is KeyboardEvent.InputConnectionStarted -> {
            val allText = event.textBefore + event.selectedText + event.textAfter
            val newCapsIndexes = allText.mapIndexedNotNull { i, c -> i.takeIf { c.isUpperCase() } }
            val selStart = event.textBefore.length
            val selEnd = event.textBefore.length + event.selectedText.length
            previousState.copy(
                textBeforeCursor = event.textBefore,
                textAfterCursor = event.textAfter,
                classInputType = event.classInputType,
                variationInputType = event.variationInputType,
                capsIndexes = newCapsIndexes,
                textSelectionStart = selStart,
                textSelectionEnd = selEnd,
                textSelectionText = event.selectedText,
                textCompositionStart = selStart,
                textCompositionEnd = selStart,
                textCompositionText = "",
                imeActionId = event.imeActionId,
            ) to if (event.selectedText.isNotEmpty()) KeyboardEffect.FinishComposing else null
        }

        is KeyboardEvent.SelectionMoved -> {
            val selEnd = event.newSelStart + event.selectedText.length
            previousState.copy(
                textBeforeCursor = event.textBefore,
                textAfterCursor = event.textAfter,
                textSelectionStart = event.newSelStart,
                textSelectionEnd = selEnd,
                textSelectionText = event.selectedText,
            ) to if (event.selectedText.isNotEmpty()) KeyboardEffect.FinishComposing else null
        }

        KeyboardEvent.InputConnectionFinished -> {
            previousState.copy(
                currentWord = null,
                currentT9code = "",
                words = emptyList(),
                capsIndexes = emptyList(),
                textBeforeCursor = "",
                textAfterCursor = "",
                textCompositionStart = 0,
                textCompositionEnd = 0,
                textCompositionText = "",
            ) to KeyboardEffect.FinishComposing
        }

        is KeyboardEvent.PreferencesApplied -> previousState.copy(
            languageSet = event.languageSet,
            themeSet = event.themeSet,
            keyboardSize = event.keyboardSize,
            hapticFeedback = event.hapticFeedback,
            backgroundColorId = event.backgroundColorId,
            isManual = event.isManualDefault,
            doubleSpaceChar = event.doubleSpaceChar,
            isAutoCaps = event.isAutoCaps,
            wordsMaxLength = event.wordsMaxLength,
        ) to null
    }
}