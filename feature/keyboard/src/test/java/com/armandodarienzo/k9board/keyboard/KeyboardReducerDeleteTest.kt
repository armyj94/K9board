package com.armandodarienzo.k9board.keyboard

import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KeyboardReducerDeleteTest {

    private lateinit var reducer: KeyboardReducer

    @Before
    fun setUp() {
        reducer = KeyboardReducer()
    }

    // region no-selection deletes → FinishComposingAndDelete

    @Test
    fun `delete with no selection emits FinishComposingAndDelete with charsBefore 1`() {
        val state = KeyboardState(textBeforeCursor = "Hello", textSelectionStart = 5)
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.FinishComposingAndDelete(1), effect)
        assertEquals("Hell", newState.textBeforeCursor)
        assertEquals(4, newState.textSelectionStart)
        assertEquals("", newState.textCompositionText)
    }

    @Test
    fun `delete in manual mode with composing char emits FinishComposingAndDelete with charsBefore 1`() {
        val state = KeyboardState(
            isManual = true,
            textBeforeCursor = "Hello x",
            textSelectionStart = 7,
            textCompositionText = "x",
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.FinishComposingAndDelete(1), effect)
        assertEquals("Hello ", newState.textBeforeCursor)
        assertEquals(6, newState.textSelectionStart)
        assertEquals("", newState.textCompositionText)
    }

    @Test
    fun `delete when nothing before cursor emits FinishComposingAndDelete(0) and leaves caps unchanged`() {
        val state = KeyboardState(
            textBeforeCursor = "",
            textSelectionStart = 0,
            capsIndexes = listOf(2),
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.FinishComposingAndDelete(0), effect)
        assertEquals("", newState.textBeforeCursor)
        assertEquals(0, newState.textSelectionStart)
        assertEquals(listOf(2), newState.capsIndexes)
    }

    // region caps-index tracking

    @Test
    fun `delete removes the cap index of the deleted char`() {
        // "He" — 'H' is capped at 0, cursor at 2, deleting 'e' at position 1
        val state = KeyboardState(
            textBeforeCursor = "He",
            textSelectionStart = 2,
            capsIndexes = listOf(0, 1),
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.FinishComposingAndDelete(1), effect)
        assertEquals(listOf(0), newState.capsIndexes)
        assertEquals("H", newState.textBeforeCursor)
        assertEquals(1, newState.textSelectionStart)
    }

    // region selection deletes → DeleteSelection

    @Test
    fun `delete with selection removes only cap indexes inside the selection`() {
        // "Hello" — 'H'=0 capped, 'l'=3 capped; selection covers [3,5) ("lo")
        val state = KeyboardState(
            textBeforeCursor = "Hel",
            textSelectionStart = 3,
            textSelectionEnd = 5,
            textSelectionText = "Lo",
            capsIndexes = listOf(0, 3),
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.DeleteSelection, effect)
        assertEquals(listOf(0), newState.capsIndexes)
        assertEquals("Hel", newState.textBeforeCursor)
        assertEquals(3, newState.textSelectionStart)
    }

    // region emoji

    @Test
    fun `delete emoji length 2 (surrogate pair) deletes entire cluster`() {
        // 😀 is a surrogate pair: 2 UTF-16 chars (length == 2)
        val emoji = "😀"
        assertEquals(2, emoji.length)
        val state = KeyboardState(
            textBeforeCursor = emoji,
            textSelectionStart = emoji.length,
            textSelectionEnd = emoji.length,
            textSelectionText = "",
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.FinishComposingAndDelete(2), effect)
        assertEquals("", newState.textBeforeCursor)
        assertEquals(0, newState.textSelectionStart)
        assertEquals("", newState.textCompositionText)
    }

    // Length 3: surrogate pair (👁 U+1F441) + variation selector-16 (U+FE0F).
    // BreakIterator treats the whole sequence as one grapheme cluster → deleted whole.
    @Test
    fun `delete emoji length 3 (surrogate pair + variation selector) deletes entire cluster`() {
        val emoji = "👁️" // U+1F441 + U+FE0F — eye with VS16, length = 3
        assertEquals(3, emoji.length)
        val state = KeyboardState(
            textBeforeCursor = emoji,
            textSelectionStart = emoji.length,
            textSelectionEnd = emoji.length,
            textSelectionText = "",
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.FinishComposingAndDelete(3), effect)
        assertEquals("", newState.textBeforeCursor)
        assertEquals(0, newState.textSelectionStart)
        assertEquals("", newState.textCompositionText)
    }

    // Length 4: two surrogate pairs — base emoji + skin-tone modifier.
    // BreakIterator treats base + modifier as one grapheme cluster → deleted whole.
    @Test
    fun `delete emoji length 4 (surrogate pair + modifier surrogate pair) deletes entire cluster`() {
        val emoji = "👍🏽" // U+1F44D + U+1F3FD — thumbs-up medium skin tone, length = 4
        assertEquals(4, emoji.length)
        val state = KeyboardState(
            textBeforeCursor = emoji,
            textSelectionStart = emoji.length,
            textSelectionEnd = emoji.length,
            textSelectionText = "",
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.FinishComposingAndDelete(4), effect)
        assertEquals("", newState.textBeforeCursor)
        assertEquals(0, newState.textSelectionStart)
        assertEquals("", newState.textCompositionText)
    }

    // Length 5: ZWJ sequence — surrogate pair + ZWJ + surrogate pair.
    // BreakIterator treats the whole ZWJ sequence as one grapheme cluster → deleted whole.
    @Test
    fun `delete ZWJ emoji length 5 deletes entire cluster`() {
        val emoji = "👨‍💻" // U+1F468 + ZWJ + U+1F4BB — man technologist, length = 5
        assertEquals(5, emoji.length)
        val state = KeyboardState(
            textBeforeCursor = emoji,
            textSelectionStart = emoji.length,
            textSelectionEnd = emoji.length,
            textSelectionText = "",
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.FinishComposingAndDelete(5), effect)
        assertEquals("", newState.textBeforeCursor)
        assertEquals(0, newState.textSelectionStart)
        assertEquals("", newState.textCompositionText)
    }

    @Test
    fun `delete selected emoji emits DeleteSelection`() {
        val emoji = "😀"
        val state = KeyboardState(
            textBeforeCursor = "Hi ",
            textSelectionStart = 3,
            textSelectionEnd = 3 + emoji.length,
            textSelectionText = emoji,
            capsIndexes = listOf(0), // 'H' capped, outside selection
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.DeleteKeyPressed)
        assertEquals(KeyboardEffect.DeleteSelection, effect)
        assertEquals(listOf(0), newState.capsIndexes) // unaffected: 0 not in [3, 5)
        assertEquals("Hi ", newState.textBeforeCursor)
        assertEquals(3, newState.textSelectionStart)
        assertEquals("", newState.textCompositionText)
    }
}