package com.armandodarienzo.k9board.keyboard

import android.util.Log
import android.view.KeyEvent
import com.armandodarienzo.k9board.keyboard.usecase.BuildT9SuggestionsUseCase
import com.armandodarienzo.k9board.keyboard.usecase.ToggleCapsStateUseCase
import com.armandodarienzo.k9board.keyboard.usecase.UpsertTypedWordsUseCase
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.repository.WordRepository
import com.armandodarienzo.k9board.repository.WordRepositoryProvider
import com.armandodarienzo.k9board.shared.ASCII_CODE_SPACE
import com.armandodarienzo.k9board.shared.WORDS_REGEX_STRING
import com.armandodarienzo.k9board.shared.substringAfterLastNotMatching
import com.armandodarienzo.k9board.shared.substringBeforeFirstNotMatching
import com.armandodarienzo.k9board.shared.model.TextComposition
import com.armandodarienzo.k9board.shared.model.TextSelection
import dagger.hilt.android.scopes.ServiceScoped
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@ServiceScoped
class KeyboardViewModel @Inject constructor(
    private val buildT9SuggestionsUseCase: BuildT9SuggestionsUseCase,
    private val upsertTypedWordsUseCase: UpsertTypedWordsUseCase,
    private val toggleCapsStateUseCase: ToggleCapsStateUseCase,
    private val wordRepositoryProvider: WordRepositoryProvider,
) {
    private val TAG = KeyboardViewModel::class.java.simpleName
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    private val _state = MutableStateFlow(KeyboardState())
    val state: StateFlow<KeyboardState> = _state.asStateFlow()

    private val _effects = Channel<KeyboardEffect>(Channel.BUFFERED)
    val effects: Flow<KeyboardEffect> = _effects.receiveAsFlow()

    // Internal text-tracking state
    private var capsIndexes = mutableListOf<Int>()
    private var textComposition = TextComposition(0, 0, "")
    private var textSelection = TextSelection(0, 0, "")
    private var textBeforeCursor = ""
    private var textAfterCursor = ""
    private var words = mutableListOf<Word>()
    private var currentWord: Word? = null
    private var currentT9code = ""
    private var wordsMaxLength = 10
    private var wordRepository: WordRepository? = null
    private var classInputType = 0
    private var variationInputType = 0
    private var wasManual = false

    // Manual-mode cycling state
    private var lastKeyId = 0
    private var keyCodesIndex = 0
    private var keyTimer = 0L

    private val wordsRegex = WORDS_REGEX_STRING.toRegex()

    fun processIntent(intent: KeyboardIntent) {
        scope.launch { handleIntent(intent) }
    }

    private suspend fun handleIntent(intent: KeyboardIntent) {
        when (intent) {
            is KeyboardIntent.T9KeyPressed -> addCharToCurrentWord(intent.digitCode)
            is KeyboardIntent.ManualKeyPressed -> handleManualKeyPressed(intent.codes, intent.keyId)
            is KeyboardIntent.SpacePressed -> commitText(ASCII_CODE_SPACE.toChar().toString())
            is KeyboardIntent.DoubleSpacePressed -> commitText(
                "${_state.value.doubleSpaceChar.value}${ASCII_CODE_SPACE.toChar()}"
            )
            is KeyboardIntent.DeletePressed -> {
                if (textSelection.text.isEmpty() && textBeforeCursor.isNotEmpty()) {
                    capsIndexes.remove(textSelection.startIndex - 1)
                } else if (textSelection.text.isNotEmpty()) {
                    capsIndexes.removeAll(textSelection.startIndex until textSelection.endIndex)
                }
                emitEffect(KeyboardEffect.SendKeyEvent(KeyEvent.KEYCODE_DEL))
            }
            is KeyboardIntent.SwapWord -> swapWord()
            is KeyboardIntent.WriteSpecificChar -> {
                commitText(intent.char)
                if (_state.value.capsStatus == KeyboardCapsStatus.UPPER_CASE) {
                    _state.update { it.copy(capsStatus = KeyboardCapsStatus.LOWER_CASE) }
                }
            }
            is KeyboardIntent.EmojiSelected -> commitText(intent.emoji)
            is KeyboardIntent.EnterManualMode -> enterManualMode()
            is KeyboardIntent.ExitManualMode -> exitManualMode()
            is KeyboardIntent.ShiftToggled -> {
                val newCaps = toggleCapsStateUseCase(
                    _state.value.capsStatus, _state.value.isManual, intent.lastShiftMs, intent.nowMs
                )
                _state.update { it.copy(capsStatus = newCaps) }
            }
            is KeyboardIntent.ImeActionPressed -> {
                _state.value.imeActionId?.let { emitEffect(KeyboardEffect.PerformEditorAction(it)) }
            }
            is KeyboardIntent.NewLinePressed -> commitText("\n")
            is KeyboardIntent.InputStarted -> handleInputStarted(intent)
            is KeyboardIntent.SelectionUpdated -> handleSelectionUpdated(intent)
            is KeyboardIntent.InputFinished -> handleInputFinished(intent)
            is KeyboardIntent.WindowShown -> {
                wordRepository = wordRepositoryProvider.getForLanguage(intent.languageTag)
            }
            is KeyboardIntent.PreferencesLoaded -> handlePreferencesLoaded(intent)
        }
    }

    private suspend fun handlePreferencesLoaded(intent: KeyboardIntent.PreferencesLoaded) {
        wordRepository = wordRepositoryProvider.getForLanguage(intent.languageSet)
        wordsMaxLength = wordRepository?.getMaxLength() ?: 10
        _state.update {
            it.copy(
                languageSet = intent.languageSet,
                themeSet = intent.themeSet,
                keyboardSize = intent.keyboardSize,
                hapticFeedback = intent.hapticFeedback,
                backgroundColorId = intent.backgroundColorId,
                isManual = intent.isManualDefault,
                doubleSpaceChar = intent.doubleSpaceChar,
                isAutoCaps = intent.isAutoCaps,
            )
        }
    }

    private suspend fun handleInputStarted(intent: KeyboardIntent.InputStarted) {
        textBeforeCursor = intent.textBefore
        textAfterCursor = intent.textAfter
        classInputType = intent.classInputType
        variationInputType = intent.variationInputType

        capsIndexes = mutableListOf()
        textSelection = TextSelection(
            textBeforeCursor.length,
            textBeforeCursor.length + intent.selectedText.length,
            intent.selectedText
        )
        (textBeforeCursor + intent.selectedText + textAfterCursor)
            .forEachIndexed { index, c -> if (c.isUpperCase()) capsIndexes.add(index) }

        textComposition = TextComposition(textBeforeCursor.length, textBeforeCursor.length, "")

        if (textSelection.length == 0) {
            setComposingRegion()
        } else {
            emitEffect(KeyboardEffect.FinishComposing)
        }

        checkAutoCaps()
        _state.update { it.copy(imeActionId = intent.imeActionId) }
    }

    private suspend fun handleSelectionUpdated(intent: KeyboardIntent.SelectionUpdated) {
        textBeforeCursor = intent.textBefore
        textAfterCursor = intent.textAfter
        textSelection.setSelection(intent.newSelStart, intent.selectedText)

        if (intent.selectedText.isNotEmpty()) {
            emitEffect(KeyboardEffect.FinishComposing)
        } else if (!_state.value.isManual) {
            setComposingRegion()
        }
        checkAutoCaps()
    }

    private suspend fun handleInputFinished(intent: KeyboardIntent.InputFinished) {
        upsertTypedWordsUseCase(
            text = intent.textBefore + intent.selectedText + intent.textAfter,
            languageTag = _state.value.languageSet,
            isPassword = inputIsPassword()
        )
        currentWord = null
        currentT9code = ""
        words.clear()
        emitEffect(KeyboardEffect.FinishComposing)
        textComposition.reset()
        capsIndexes.clear()
        textBeforeCursor = ""
        textAfterCursor = ""
    }

    private suspend fun addCharToCurrentWord(digitCode: Int) {
        val digit = digitCode.toChar()
        updateCurrentWord(digit)

        val currentWordCharArray = currentWord!!.text.toCharArray()
        val wordTextBeforeCursor = getWordTextBeforeCursor()
        val capsStatus = _state.value.capsStatus

        if (capsStatus == KeyboardCapsStatus.UPPER_CASE || capsStatus == KeyboardCapsStatus.CAPS_LOCK) {
            capsIndexes.add(textSelection.startIndex)
            currentWordCharArray[wordTextBeforeCursor.length] =
                currentWordCharArray[wordTextBeforeCursor.length].uppercaseChar()
            if (capsStatus == KeyboardCapsStatus.UPPER_CASE) {
                _state.update { it.copy(capsStatus = KeyboardCapsStatus.LOWER_CASE) }
            }
        }

        getCapsIndexesOfCurrentWord().forEach { idx ->
            currentWordCharArray[idx] = currentWordCharArray[idx].uppercaseChar()
        }

        setComposingText(String(currentWordCharArray))
    }

    private fun handleManualKeyPressed(codes: IntArray, keyId: Int) {
        if (lastKeyId == keyId && System.currentTimeMillis() - keyTimer < LONG_PRESSURE_TIME_MILLIS) {
            keyCodesIndex = if (keyCodesIndex < codes.size - 1) keyCodesIndex + 1 else 0
        } else {
            finishComposing()
            if (_state.value.capsStatus != KeyboardCapsStatus.LOWER_CASE) {
                capsIndexes.add(textSelection.startIndex)
            }
            keyCodesIndex = 0
        }
        addCodeToCurrentText(codes[keyCodesIndex])
        lastKeyId = keyId
        keyTimer = System.currentTimeMillis()
    }

    private fun addCodeToCurrentText(inputCode: Int) {
        val iToByteArray = String(intArrayOf(inputCode), 0, 1).toByteArray(Charsets.UTF_16)
        val code = String(iToByteArray, Charsets.UTF_16).toCharArray()
        setComposingText(String(code))
    }

    private fun swapWord() {
        val currentIndex = words.indexOf(currentWord)
        try {
            currentWord = words[currentIndex + 1]
            Log.d(TAG, "new currentWord = ${currentWord!!.text}")
            val currentWordCharArray = currentWord!!.text.toCharArray()
            getCapsIndexesOfCurrentWord().forEach { idx ->
                currentWordCharArray[idx] = currentWordCharArray[idx].uppercaseChar()
            }
            setComposingText(String(currentWordCharArray))
        } catch (e: IndexOutOfBoundsException) {
            enterManualMode()
        }
    }

    private fun enterManualMode() {
        if (_state.value.capsStatus == KeyboardCapsStatus.UPPER_CASE) {
            _state.update { it.copy(capsStatus = KeyboardCapsStatus.CAPS_LOCK) }
        }
        wasManual = _state.value.isManual
        _state.update { it.copy(isManual = true) }
        finishComposing()
    }

    private fun exitManualMode() {
        if (!wasManual) _state.update { it.copy(isManual = false) }
        wasManual = false
    }

    private suspend fun setComposingRegion() {
        val wordTextBeforeCursor = getWordTextBeforeCursor()
        val wordTextAfterCursor = getWordTextAfterCursor()

        val composingStartIndex = if (wordTextBeforeCursor.isNotEmpty())
            textBeforeCursor.lastIndexOf(wordTextBeforeCursor)
        else
            textBeforeCursor.length

        val composingEndIndex = composingStartIndex + wordTextBeforeCursor.length + wordTextAfterCursor.length

        updateCurrentWord(null)
        textComposition.setRegion(composingStartIndex, wordTextBeforeCursor + wordTextAfterCursor)
        emitEffect(KeyboardEffect.SetComposingRegion(composingStartIndex, composingEndIndex))
    }

    private fun setComposingText(newText: String) {
        textComposition.setText(newText)
        emitEffect(KeyboardEffect.SetComposingText(newText))
    }

    private fun commitText(s: String) {
        finishComposing()
        textComposition.reset(textBeforeCursor.length + s.length)
        if (_state.value.capsStatus != KeyboardCapsStatus.LOWER_CASE) {
            capsIndexes.add(textSelection.startIndex)
        }
        emitEffect(KeyboardEffect.CommitText(s))
    }

    private fun finishComposing() {
        textComposition.reset(textBeforeCursor.length)
        emitEffect(KeyboardEffect.FinishComposing)
    }

    private suspend fun updateCurrentWord(newCode: Char?) {
        val wordTextBeforeCursor = getWordTextBeforeCursor()
        val wordTextAfterCursor = getWordTextAfterCursor()
        currentT9code = Word.getNumberDigitsCode(wordTextBeforeCursor) + (newCode ?: "") +
                Word.getNumberDigitsCode(wordTextAfterCursor)

        words = buildT9SuggestionsUseCase(
            currentT9code, _state.value.languageSet, wordsMaxLength
        ).toMutableList()

        currentWord = if (newCode == null) {
            words.firstOrNull {
                it.text.compareTo(wordTextBeforeCursor + wordTextAfterCursor, ignoreCase = true) == 0
            } ?: Word(wordTextBeforeCursor + wordTextAfterCursor)
        } else {
            if (words.isNotEmpty()) words.first()
            else Word(wordTextBeforeCursor + newCode + wordTextAfterCursor)
        }
    }

    private fun getWordTextBeforeCursor(): String =
        textBeforeCursor.substringAfterLastNotMatching(wordsRegex)

    private fun getWordTextAfterCursor(): String =
        textAfterCursor.substringBeforeFirstNotMatching(wordsRegex)


    private fun getCapsIndexesOfCurrentWord(): List<Int> {
        val wordStart = textSelection.startIndex - getWordTextBeforeCursor().length
        val wordEnd = textSelection.startIndex + getWordTextAfterCursor().length
        return capsIndexes
            .filter { it >= wordStart && it <= wordEnd + 1 }
            .map { it - wordStart }
    }

    private fun checkAutoCaps() {
        if (_state.value.isAutoCaps &&
            (textBeforeCursor.trimEnd().endsWith(".") ||
             textBeforeCursor.trimEnd().endsWith("?") ||
             textBeforeCursor.trimEnd().endsWith("!"))
        ) {
            _state.update { it.copy(capsStatus = KeyboardCapsStatus.UPPER_CASE) }
        }
    }

    private fun inputIsPassword(): Boolean {
        val variation = variationInputType
        return variation == android.text.InputType.TYPE_TEXT_VARIATION_PASSWORD ||
               variation == android.text.InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD ||
               variation == android.text.InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    }

    private fun emitEffect(effect: KeyboardEffect) {
        _effects.trySend(effect)
    }

    fun clear() {
        scope.cancel()
        _effects.close()
    }

    companion object {
        private const val LONG_PRESSURE_TIME_MILLIS = 500L
    }
}

