package com.armandodarienzo.k9board.keyboard

import com.armandodarienzo.k9board.model.Word
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class KeyboardReducerSwapWordTest {

    private lateinit var reducer: KeyboardReducer

    @Before
    fun setUp() {
        reducer = KeyboardReducer()
    }

    // region basic swap

    @Test
    fun `swap word emits SetComposingText with new word text`() {
        val ne = Word("ne")
        val me = Word("me")
        val state = KeyboardState(
            textBeforeCursor = "ne",
            textSelectionStart = 2,
            words = listOf(ne, me),
            currentWord = ne,
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.WordSwapped(me))
        assertEquals(KeyboardEffect.SetComposingText("me"), effect)
        assertEquals(me, newState.currentWord)
        assertEquals("me", newState.textCompositionText)
    }

    @Test
    fun `swap word does not modify the words list`() {
        val ne = Word("ne")
        val me = Word("me")
        val oe = Word("oe")
        val words = listOf(ne, me, oe)
        val state = KeyboardState(
            textBeforeCursor = "ne",
            textSelectionStart = 2,
            words = words,
            currentWord = ne,
        )
        val (newState, _) = reducer.reduce(state, KeyboardEvent.WordSwapped(me))
        assertEquals(words, newState.words)
    }

    @Test
    fun `swap word does not modify textBeforeCursor or cursor position`() {
        val ne = Word("ne")
        val me = Word("me")
        val state = KeyboardState(
            textBeforeCursor = "ne",
            textSelectionStart = 2,
            words = listOf(ne, me),
            currentWord = ne,
        )
        val (newState, _) = reducer.reduce(state, KeyboardEvent.WordSwapped(me))
        assertEquals("ne", newState.textBeforeCursor)
        assertEquals(2, newState.textSelectionStart)
    }

    // region caps

    @Test
    fun `swap word uppercases first char when caps index covers word start`() {
        val ne = Word("ne")
        val me = Word("me")
        // "ne" starts at position 0; capsIndexes=[0] marks its first char
        val state = KeyboardState(
            textBeforeCursor = "ne",
            textSelectionStart = 2,
            words = listOf(ne, me),
            currentWord = ne,
            capsIndexes = listOf(0),
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.WordSwapped(me))
        assertEquals(KeyboardEffect.SetComposingText("Me"), effect)
        assertEquals("Me", newState.textCompositionText)
    }

    @Test
    fun `swap word uppercases correct char when word is not at start of textBeforeCursor`() {
        val ne = Word("ne")
        val me = Word("me")
        // "Hello ne" — word "ne" starts at offset 6; capsIndexes=[0,6] marks 'H' and 'n'
        val state = KeyboardState(
            textBeforeCursor = "Hello ne",
            textSelectionStart = 8,
            words = listOf(ne, me),
            currentWord = ne,
            capsIndexes = listOf(0, 6),
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.WordSwapped(me))
        assertEquals(KeyboardEffect.SetComposingText("Me"), effect)
        assertEquals("Me", newState.textCompositionText)
    }

    @Test
    fun `swap word leaves text lowercase when no caps index covers the current word`() {
        val ne = Word("ne")
        val me = Word("me")
        // "Hello ne" — capsIndexes=[0] only covers 'H', not "ne" which starts at 6
        val state = KeyboardState(
            textBeforeCursor = "Hello ne",
            textSelectionStart = 8,
            words = listOf(ne, me),
            currentWord = ne,
            capsIndexes = listOf(0),
        )
        val (newState, effect) = reducer.reduce(state, KeyboardEvent.WordSwapped(me))
        assertEquals(KeyboardEffect.SetComposingText("me"), effect)
        assertEquals("me", newState.textCompositionText)
    }
}