package com.armandodarienzo.k9board.keyboard

import com.armandodarienzo.k9board.keyboard.usecase.BuildT9SuggestionsUseCase
import com.armandodarienzo.k9board.keyboard.usecase.ToggleCapsStateUseCase
import com.armandodarienzo.k9board.keyboard.usecase.UpsertTypedWordsUseCase
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.repository.WordRepository
import com.armandodarienzo.k9board.repository.WordRepositoryProvider
import com.armandodarienzo.k9board.shared.ASCII_CODE_SPACE
import com.armandodarienzo.k9board.shared.ui.base.ChannelEffectDelegate
import com.armandodarienzo.k9board.shared.ui.base.MviProcessor
import com.armandodarienzo.k9board.shared.ui.base.MviStoreDelegate
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@ServiceScoped
class KeyboardViewModel @Inject constructor(
    private val buildT9SuggestionsUseCase: BuildT9SuggestionsUseCase,
    private val upsertTypedWordsUseCase: UpsertTypedWordsUseCase,
    private val toggleCapsStateUseCase: ToggleCapsStateUseCase,
    private val wordRepositoryProvider: WordRepositoryProvider,
) : MviProcessor<KeyboardState, KeyboardAction, KeyboardEffect> {

    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private val effectDelegate = ChannelEffectDelegate<KeyboardEffect>()
    private val mviStore = MviStoreDelegate(
        initialState = KeyboardState(),
        scope = scope,
        reducer = KeyboardReducer(),
        effectDelegate = effectDelegate,
    )

    override val state: StateFlow<KeyboardState> = mviStore.state
    override val effect: Flow<KeyboardEffect> = effectDelegate.effect

    // wordRepository is a service dependency, not serialisable state
    private var wordRepository: WordRepository? = null

    override fun processAction(action: KeyboardAction) {
        scope.launch { handleAction(action) }
    }

    private suspend fun handleAction(action: KeyboardAction) {
        when (action) {
            is KeyboardAction.T9KeyPressed -> handleT9KeyPressed(action.digitCode)
            is KeyboardAction.ManualKeyPressed -> handleManualKeyPressed(action.codes, action.keyId)
            KeyboardAction.SpacePressed -> {
                mviStore.sendEvent(KeyboardEvent.TextCommitted(ASCII_CODE_SPACE.toChar().toString()))
            }
            KeyboardAction.DoubleSpacePressed -> {
                val char = mviStore.state.value.doubleSpaceChar.value
                mviStore.sendEvent(
                    KeyboardEvent.TextCommitted("$char${ASCII_CODE_SPACE.toChar()}")
                )
            }
            KeyboardAction.DeletePressed -> mviStore.sendEvent(KeyboardEvent.DeleteKeyPressed)
            KeyboardAction.SwapWord -> handleSwapWord()
            is KeyboardAction.WriteSpecificChar -> mviStore.sendEvent(
                KeyboardEvent.TextCommitted(action.char, lowerCaseAfterCommit = true)
            )
            is KeyboardAction.EmojiSelected -> mviStore.sendEvent(
                KeyboardEvent.TextCommitted(action.emoji)
            )
            KeyboardAction.EnterManualMode -> mviStore.sendEvent(KeyboardEvent.ManualModeEntered)
            KeyboardAction.ExitManualMode -> mviStore.sendEvent(KeyboardEvent.ManualModeExited)
            is KeyboardAction.ShiftToggled -> {
                val s = mviStore.state.value
                val newCaps = toggleCapsStateUseCase(s.capsStatus, s.isManual, action.lastShiftMs, action.nowMs)
                mviStore.sendEvent(KeyboardEvent.CapsStatusUpdated(newCaps))
            }
            KeyboardAction.ImeActionPressed -> {
                mviStore.state.value.imeActionId?.let {
                    mviStore.sendEvent(KeyboardEvent.ImeActionTriggered(it))
                }
            }
            KeyboardAction.NewLinePressed -> mviStore.sendEvent(KeyboardEvent.TextCommitted("\n"))
            is KeyboardAction.InputStarted -> handleInputStarted(action)
            is KeyboardAction.SelectionUpdated -> handleSelectionUpdated(action)
            is KeyboardAction.InputFinished -> handleInputFinished(action)
            is KeyboardAction.WindowShown -> {
                wordRepository = wordRepositoryProvider.getForLanguage(action.languageTag)
            }
            is KeyboardAction.PreferencesLoaded -> handlePreferencesLoaded(action)
        }
    }

    private suspend fun handleT9KeyPressed(digitCode: Int) {
        val state = mviStore.state.value
        val wBefore = state.getWordTextBeforeCursor()
        val wAfter = state.getWordTextAfterCursor()
        val digit = digitCode.toChar()
        val newCode = Word.getNumberDigitsCode(wBefore) + digit + Word.getNumberDigitsCode(wAfter)
        val suggestions = buildT9SuggestionsUseCase(newCode, state.languageSet, state.wordsMaxLength)
        mviStore.sendEvent(KeyboardEvent.T9WordsReady(suggestions, newCode, digit))
    }

    private fun handleManualKeyPressed(codes: IntArray, keyId: Int) {
        val state = mviStore.state.value
        val now = System.currentTimeMillis()
        val isNewWord = state.lastKeyId != keyId || now - state.keyTimer >= LONG_PRESSURE_TIME_MILLIS
        val currentIndex = if (!isNewWord && state.keyCodesIndex < codes.size - 1)
            state.keyCodesIndex else -1
        val newIndex = if (currentIndex >= 0) currentIndex + 1 else 0
        mviStore.sendEvent(KeyboardEvent.ManualCharAdded(codes[newIndex], isNewWord, keyId, now))
    }

    private fun handleSwapWord() {
        val state = mviStore.state.value
        val idx = state.words.indexOf(state.currentWord)
        if (idx >= 0 && idx + 1 < state.words.size) {
            mviStore.sendEvent(KeyboardEvent.WordSwapped(state.words[idx + 1]))
        } else {
            mviStore.sendEvent(KeyboardEvent.ManualModeEntered)
        }
    }

    private suspend fun handleInputStarted(action: KeyboardAction.InputStarted) {
        mviStore.sendEvent(
            KeyboardEvent.InputConnectionStarted(
                textBefore = action.textBefore,
                textAfter = action.textAfter,
                selectedText = action.selectedText,
                classInputType = action.classInputType,
                variationInputType = action.variationInputType,
                imeActionId = action.imeActionId,
            )
        )
        if (action.selectedText.isEmpty()) dispatchComposingRegion()
        checkAutoCaps()
    }

    private suspend fun handleSelectionUpdated(action: KeyboardAction.SelectionUpdated) {
        mviStore.sendEvent(
            KeyboardEvent.SelectionMoved(
                textBefore = action.textBefore,
                textAfter = action.textAfter,
                selectedText = action.selectedText,
                newSelStart = action.newSelStart,
            )
        )
        if (action.selectedText.isEmpty() && !mviStore.state.value.isManual) {
            dispatchComposingRegion()
        }
        checkAutoCaps()
    }

    private suspend fun handleInputFinished(action: KeyboardAction.InputFinished) {
        val state = mviStore.state.value
        upsertTypedWordsUseCase(
            text = action.textBefore + action.selectedText + action.textAfter,
            languageTag = state.languageSet,
            isPassword = state.inputIsPassword(),
        )
        mviStore.sendEvent(KeyboardEvent.InputConnectionFinished)
    }

    private suspend fun handlePreferencesLoaded(action: KeyboardAction.PreferencesLoaded) {
        wordRepository = wordRepositoryProvider.getForLanguage(action.languageSet)
        val maxLength = wordRepository?.getMaxLength() ?: 10
        mviStore.sendEvent(
            KeyboardEvent.PreferencesApplied(
                languageSet = action.languageSet,
                themeSet = action.themeSet,
                keyboardSize = action.keyboardSize,
                hapticFeedback = action.hapticFeedback,
                backgroundColorId = action.backgroundColorId,
                isManualDefault = action.isManualDefault,
                doubleSpaceChar = action.doubleSpaceChar,
                isAutoCaps = action.isAutoCaps,
                wordsMaxLength = maxLength,
            )
        )
    }

    private suspend fun dispatchComposingRegion() {
        val state = mviStore.state.value
        val wBefore = state.getWordTextBeforeCursor()
        val wAfter = state.getWordTextAfterCursor()
        val code = Word.getNumberDigitsCode(wBefore) + Word.getNumberDigitsCode(wAfter)
        val suggestions = buildT9SuggestionsUseCase(code, state.languageSet, state.wordsMaxLength)
        val composingStart = if (wBefore.isNotEmpty())
            state.textBeforeCursor.lastIndexOf(wBefore)
        else
            state.textBeforeCursor.length
        val composingEnd = composingStart + wBefore.length + wAfter.length
        val currentWord = suggestions.firstOrNull {
            it.text.compareTo(wBefore + wAfter, ignoreCase = true) == 0
        } ?: Word(wBefore + wAfter)
        mviStore.sendEvent(KeyboardEvent.ComposingRegionReady(composingStart, composingEnd, currentWord))
    }

    private fun checkAutoCaps() {
        val state = mviStore.state.value
        if (state.isAutoCaps) {
            val trimmed = state.textBeforeCursor.trimEnd()
            if (trimmed.endsWith(".") || trimmed.endsWith("?") || trimmed.endsWith("!")) {
                mviStore.sendEvent(KeyboardEvent.AutoCapsApplied(KeyboardCapsStatus.UPPER_CASE))
            }
        }
    }

    fun clear() {
        scope.cancel()
        effectDelegate.close()
    }

    companion object {
        private const val LONG_PRESSURE_TIME_MILLIS = 500L
    }
}