package com.armandodarienzo.k9board.shared.service

import android.inputmethodservice.InputMethodService
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.CursorAnchorInfo
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import androidx.compose.runtime.mutableStateOf
import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.shared.ASCII_CODE_SPACE
import com.armandodarienzo.k9board.shared.WORDS_REGEX_STRING
import com.armandodarienzo.k9board.shared.substringAfterLastNotMatching
import com.armandodarienzo.k9board.shared.substringBeforeFirstNotMatching
import com.armandodarienzo.k9board.viewmodel.DictionaryDataHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlin.system.measureTimeMillis

@AndroidEntryPoint
open class Key9Service : InputMethodService(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    lateinit var view: View

    private val TAG = Companion::class.java.simpleName


    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)

    //DbDataHelper
    lateinit var db: DictionaryDataHelper
    var meanFrequency: Int = 0
    var wordsMaxLength: Int = 10

    var words = mutableListOf<Word>()
    var currentWord: Word? = null

    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry = savedStateRegistryController.savedStateRegistry

    var cursorPosition: Int = 0
    var previousTextLength = 0

    var currentT9code: String = ""

    lateinit var indexesOfCaps: MutableList<Int>


    var isCaps = mutableStateOf(KeyboardCapsStatus.LOWER_CASE)
    var isManual = mutableStateOf(false)
    private var lastKeyId: Int? = 0
    private var keyCodesIndex: Int = 0
    private var keyTimer = 0L


    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        super.onStartInputView(info, restarting)
    }

    override fun onCreateInputView(): View {

//        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        val setLanguage = prefs.getString(
//            this.getString(R.string.shared_prefs_set_language),
//            LANGUAGE_TAG_ENGLISH_AMERICAN
//        )
//        val dbBaseName = this.getString(R.string.db_base_name)
//        db = DictionaryDataHelper(this, "${dbBaseName}_${setLanguage}.sqlite")
        db = DictionaryDataHelper(this, "dictionary.sqlite")
        db.writableDatabase.enableWriteAheadLogging()//db.readableDatabase
        db.writableDatabase.execSQL("PRAGMA synchronous = NORMAL")

        indexesOfCaps = mutableListOf()

        //TODO: fetch user preference
        isCaps.value = KeyboardCapsStatus.LOWER_CASE


        GlobalScope.async {

            var getFrequencyTime = measureTimeMillis {
                meanFrequency = db.getMeanFrequency()
                wordsMaxLength = db.getMaxLength()
            }
            Log.d(TAG, "Time: $getFrequencyTime")

        }


        return view
    }

    //Lifecylce Methods



    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

    private fun handleLifecycleEvent(event: Lifecycle.Event) =
        lifecycleRegistry.handleLifecycleEvent(event)

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
    }

    override fun onDestroy() {
        super.onDestroy()
        handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    }

    override fun onUpdateCursorAnchorInfo(cursorAnchorInfo: CursorAnchorInfo?) {

        super.onUpdateCursorAnchorInfo(cursorAnchorInfo)

        var indexesofCapsString: String = ""
        indexesOfCaps.forEach { indexesofCapsString += it }


        val textBeforeCursor = getTextBeforeCursor()
        val textAfterCursor = getTextAfterCursor()
//        inputConnection = currentInputConnection //Riassegnare la inputConnection previene i crash quando apro e chiudo una TextEdit all'interno della stessa activity


        cursorPosition = textBeforeCursor.length

        updateCurrentWord(null)


        if (previousTextLength != textBeforeCursor.length + textAfterCursor.length) {

            indexesOfCaps =
                indexesOfCaps.map { if (it > cursorPosition) it + textBeforeCursor.length + textAfterCursor.length - previousTextLength else it }
                    .toMutableList()

        }


        previousTextLength = textBeforeCursor.length + textAfterCursor.length


    }

    //ViewModelStore Methods
    private val store = ViewModelStore()

    override fun getViewModelStore(): ViewModelStore = store

    fun getTextBeforeCursor(): CharSequence{
        return currentInputConnection.getTextBeforeCursor(5000, 0)?: ""
    }

    fun getTextAfterCursor(): CharSequence{
        return currentInputConnection.getTextAfterCursor(5000, 0)?: ""
    }

    fun getWordTextBeforeCursor(): String{
        var text = currentInputConnection.getTextBeforeCursor(100, 0)?: ""
        return text.toString().substringAfterLastNotMatching(WORDS_REGEX)
    }

    fun getWordTextAfterCursor(): String{
        val text = currentInputConnection.getTextAfterCursor(100, 0)?: ""
        return text.toString().substringBeforeFirstNotMatching(WORDS_REGEX)
    }

    fun updateCurrentWord(newCode: Char?){

        var attempt: Int = 1

        val wordTextBeforeCursor = getWordTextBeforeCursor()
        val wordTextAfterCursor = getWordTextAfterCursor()
        currentT9code = Word.getNumberDigitsCode(wordTextBeforeCursor) + (newCode?:"") + Word.getNumberDigitsCode(
            wordTextAfterCursor
        )

        words = db.getWordsByCode(currentT9code)


        if(newCode == null){

            currentWord = words.filter { word: Word ->  (word.text.compareTo( wordTextBeforeCursor + wordTextAfterCursor, ignoreCase = true) == 0) }.firstOrNull()?: Word(
                wordTextBeforeCursor + wordTextAfterCursor
            )

        } else {

            if (words.isNotEmpty()){
                currentWord = words.first()
            } else if (words.isEmpty() && currentT9code.isNotEmpty()){

                if(currentT9code.length > 2){ //Ottimizzazione per inizio composizione
                    while (words.isEmpty() && currentT9code.length + attempt <= wordsMaxLength ){
                        words.addAll(db.gePlaceholderWordsByCode(currentT9code, attempt))
                        attempt += 1
                    }
                }


                currentWord = if (words.isNotEmpty()) words.first()
                                else Word(
                    wordTextBeforeCursor + newCode.toString()
                            + wordTextAfterCursor
                )

            }

        }

    }

    fun getCapsIndexesOfCurrentWord(): List<Int>{

        var relativeCapsIndexes = listOf<Int>()

//    Log.d("COMMON", "cursorPosition: $cursorPosition")
//    Log.d("COMMON", "getWordTextBeforeCursor().length: ${textBeforeCursor.length}")
//    Log.d("COMMON", "getWordTextAfterCursor().length: ${textAfterCursor.length}")
        relativeCapsIndexes = indexesOfCaps.filter {
            it >= cursorPosition - getWordTextBeforeCursor().length
                    && it <= cursorPosition + getWordTextAfterCursor().length + 1
        }.map { it - (cursorPosition - getWordTextBeforeCursor().length) }


//    Log.d("COMMON", "Indexes of Caps filtered: ${indexesOfCaps.filter { it >= cursorPosition - getWordTextBeforeCursor().length && it <= cursorPosition + getWordTextAfterCursor().length }}")

        return relativeCapsIndexes

    }

    fun deleteChar(){

        //Gestisco le maiuscole
        indexesOfCaps.remove(cursorPosition)

        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
        val upEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL)
        currentInputConnection?.sendKeyEvent(downEvent)
        currentInputConnection?.sendKeyEvent(upEvent)

        currentInputConnection.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)

    }

    private fun addCharToCurrentWord(code: Int) {

        val digit = code.toChar()

        updateCurrentWord(digit)

        var currentWordCharArray = currentWord!!.text.toCharArray()

        val wordTextBeforeCursor = getWordTextBeforeCursor()
        val wordTextAfterCursor = getWordTextAfterCursor()


        currentInputConnection.deleteSurroundingText(
            wordTextBeforeCursor.length,
            wordTextAfterCursor.length
        )

        if (isCaps.value == KeyboardCapsStatus.UPPER_CASE || isCaps.value == KeyboardCapsStatus.CAPS_LOCK) {

            indexesOfCaps.add(cursorPosition + 1)
            currentWordCharArray[wordTextBeforeCursor.length] =
                currentWordCharArray[wordTextBeforeCursor.length].toUpperCase()

            if (isCaps.value == KeyboardCapsStatus.UPPER_CASE) {
                isCaps.value = KeyboardCapsStatus.LOWER_CASE
            }

        }


        var capsIndexesOfCurrentWord = getCapsIndexesOfCurrentWord()
        capsIndexesOfCurrentWord.forEach {

            currentWordCharArray[it - 1] = currentWordCharArray[it - 1].toUpperCase()

        }

        currentInputConnection.commitText(String(currentWordCharArray), 1)

    }

    private fun addCodeToCurrentText(inputCode: Int) {

        var iToByteArray = String(intArrayOf(inputCode), 0, 1).toByteArray(Charsets.UTF_16)
        var code = String(iToByteArray, Charsets.UTF_16).toCharArray()

        if (isCaps.value == KeyboardCapsStatus.UPPER_CASE) {
            /*è inutile aggiungere indici di caratteri Maiuscoli in modalità manuale
            * dato che essenzialmente la modalità manuale è prevista per parole che non esistono in DB.
            * Se l'utente scrive in modalità manuale, non ha bisogno di premere il tasto SWAP e quindi non potrà mai invalidare le maiuscole che ha inserito in tal modo*/
            isCaps.value = KeyboardCapsStatus.LOWER_CASE

        }

        currentInputConnection.commitText(String(code), 1)

    }

    fun keyClick(codes: IntArray,
                 forceManual: Boolean,
                 keyId: Int) {


        if (isManual.value || forceManual) {

            if (lastKeyId == keyId && System.currentTimeMillis() - keyTimer < LONG_PRESSURE_TIME_MILLIS) {

                deleteChar()
                if (keyCodesIndex < codes.size - 1) {
                    keyCodesIndex += 1
                } else {
                    keyCodesIndex = 0
                }


            } else {

                keyCodesIndex = 0

            }

            addCodeToCurrentText(codes[keyCodesIndex]) //TODO: sistemare caps e modalità manuale
            lastKeyId = keyId
            keyTimer = System.currentTimeMillis()

        } else {
            Log.d(TAG, "isNotManual")
            var digitCode = codes.last()

            if (digitCode != null) addCharToCurrentWord(digitCode)
        }
    }

    fun spaceClick() {
//        val nowInMillis = System.currentTimeMillis()

        val code = ASCII_CODE_SPACE.toChar()
//        if (currentInputConnection.getTextBeforeCursor(2, 0)
//                .toString() == ", " && prefs.getBoolean(
//                this.getString(R.string.user_prefs_dot_double_space_key),
//                false
//            ) && nowInMillis - spaceKeyTimer < LONG_PRESSURE_TIME_MILLIS
//        ) {
//            currentInputConnection.deleteSurroundingText(2, 0)
//            currentInputConnection.commitText(".", 1)
//            //                if (!isCaps) onPress(Keyboard.KEYCODE_SHIFT) TODO: fixare e riabilitare
//        }
//        if (currentInputConnection.getTextBeforeCursor(1, 0).toString() == " " && prefs.getBoolean(
//                service.getString(R.string.user_prefs_dot_double_space_key),
//                false
//            ) && nowInMillis - spaceKeyTimer < LONG_PRESSURE_TIME_MILLIS
//        ) {
//            currentInputConnection.deleteSurroundingText(1, 0)
//            if (prefs.getBoolean(
//                    service.getString(R.string.user_prefs_dot_triple_space_key),
//                    false
//                )
//            ) currentInputConnection.commitText(",", 1)
//            else {
//                currentInputConnection.commitText(".", 1)
//                //                    if (!isCaps) onPress(Keyboard.KEYCODE_SHIFT) TODO: fixare e riabilitare
//            }
//        }

        currentInputConnection.commitText(code.toString(), 1)
//        spaceKeyTimer = System.currentTimeMillis()
    }

    fun swapClick() {
        if (isManual.value) {

            isManual.value = false

        } else {

            currentInputConnection.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)
                .apply {

                    var currentWordCharArray: CharArray


                    val currentIndex = words.indexOf(currentWord)

                    try {

                        currentWord = words[currentIndex + 1]
                        currentWordCharArray = currentWord!!.text.toCharArray()
//                        Log.d(TAG, "indexesOfCaps = $indexesOfCaps")
//                        Log.d(TAG, "getCapsIndexesOfCurrentWord = ${getCapsIndexesOfCurrentWord()}")
                        getCapsIndexesOfCurrentWord().forEach {

                            currentWordCharArray[it - 1] =
                                currentWordCharArray[it - 1].toUpperCase()

                        }


                        currentInputConnection.deleteSurroundingText(
                            getWordTextBeforeCursor().length,
                            getWordTextAfterCursor().length
                        )

                        currentInputConnection.commitText(String(currentWordCharArray), 1)
                        currentInputConnection.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)

//                        currentInputConnection.setComposingRegion(getWordTextAfterCursor().length, getWordTextBeforeCursor().length);
//                        currentInputConnection.setComposingText(String(currentWordCharArray), 1)
//                        currentInputConnection.requestCursorUpdates(InputConnection.CURSOR_UPDATE_IMMEDIATE)
                    } catch (e: IndexOutOfBoundsException) {

                        Log.d(TAG, "swapClick isManual $isManual")
//                        changeModeToManual()
//                        inputConnection.deleteSurroundingText(getWordTextBeforeCursor().length, getWordTextAfterCursor().length)
                        if (isCaps.value == KeyboardCapsStatus.UPPER_CASE) isCaps.value =
                            KeyboardCapsStatus.CAPS_LOCK
//                    if (prefs.getString(service.getString(R.string.shared_prefs_set_language), service.getString(R.string.language_tag_english_american)) == service.getString(R.string.language_tag_russian)){
//                        changeKeyboardviewTo(t9ManualKeyboardRussian)
//                    } else changeKeyboardviewTo(t9ManualKeyboard)
                        isManual.value = true

                    }

                }

        }
    }

    fun emojiClick(emojiViewItem: EmojiViewItem) {
        val emoji : String = emojiViewItem.emoji

        currentInputConnection.commitText(emoji, 1)
    }




//    fun isManual() : Boolean {
//        return isManual;
//    }
//
//    fun setManual(value: Boolean) {
//        isManual = value;
//    }

    companion object {
        const val LONG_PRESSURE_TIME_MILLIS = 500L
        val WORDS_REGEX = WORDS_REGEX_STRING.toRegex()
    }
}