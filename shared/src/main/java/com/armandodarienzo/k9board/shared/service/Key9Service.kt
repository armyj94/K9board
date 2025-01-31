package com.armandodarienzo.k9board.shared.service

import android.content.Context
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.inputmethodservice.ExtractEditText
import android.inputmethodservice.InputMethodService
import android.os.Build
import android.text.InputType
import android.util.DisplayMetrics
import android.util.Log
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.FrameLayout
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import androidx.lifecycle.ViewModelStore
import androidx.lifecycle.ViewModelStoreOwner
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.setViewTreeLifecycleOwner
import androidx.lifecycle.setViewTreeViewModelStoreOwner
import androidx.savedstate.SavedStateRegistryController
import androidx.savedstate.SavedStateRegistryOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.shared.ASCII_CODE_SPACE
import com.armandodarienzo.k9board.shared.USER_WORDS_FLAG
import com.armandodarienzo.k9board.shared.WORDS_REGEX_STRING
import com.armandodarienzo.k9board.shared.WORDS_SPACE_REGEX_STRING
import com.armandodarienzo.k9board.shared.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.shared.model.TextComposition
import com.armandodarienzo.k9board.shared.model.TextSelection
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepositoryLocal
import com.armandodarienzo.k9board.shared.repository.dataStore
import com.armandodarienzo.k9board.shared.substringAfterLastNotMatching
import com.armandodarienzo.k9board.shared.substringBeforeFirstNotMatching
import com.armandodarienzo.k9board.shared.DATABASE_NAME
import com.armandodarienzo.k9board.shared.ui.KeyboardProvider
import com.armandodarienzo.k9board.shared.ui.keyboard.ComposeKeyboardView
import com.armandodarienzo.k9board.viewmodel.DictionaryDataHelper
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import javax.inject.Inject
import kotlin.math.sqrt
import kotlin.system.measureTimeMillis


@AndroidEntryPoint
open class Key9Service : InputMethodService(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    @Inject
    lateinit var keyboardProvider: KeyboardProvider

    var backgroundColorId: Int = 0
    lateinit var view: View

    private val TAG = Key9Service::class.java.simpleName

    private var lifecycleRegistry: LifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = this.lifecycleRegistry
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry = savedStateRegistryController.savedStateRegistry

    override val viewModelStore: ViewModelStore = ViewModelStore()

    var classInputType = 0
    var variationInputType = 0

    //DbDataHelper
    lateinit var db: DictionaryDataHelper
    var meanFrequency: Int = 0
    var wordsMaxLength: Int = 10

    var words = mutableListOf<Word>()
    var currentWord: Word? = null


    var currentT9code: String = ""

    lateinit var capsIndexes: MutableList<Int>


    var isCaps = mutableStateOf(KeyboardCapsStatus.UPPER_CASE)
    var isAutoCaps = mutableStateOf(false)

    private var _textLengthState = mutableIntStateOf(0)
    val textLengthState = _textLengthState

    private var _doubleSpaceCharState = mutableStateOf(DoubleSpaceCharacter.NONE)
    val doubleSpaceCharState = _doubleSpaceCharState

    var isManual = mutableStateOf(false)
    private var wasManual = isManual.value

    private var lastKeyId: Int = 0
    private var keyCodesIndex: Int = 0
    private var keyTimer = 0L

    private lateinit var textSelection: TextSelection
    private lateinit var textBeforeCursor: String
    private lateinit var textAfterCursor: String

    private lateinit var textComposition: TextComposition

    /*We access directly the repository because it is not possible to
    * inject a hiltViewModel in an AbstractComposeView at the moment*/
    private lateinit var userPreferencesRepository : UserPreferencesRepositoryLocal

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
                /* This is needed because otherwise recomposition wont be triggered
        *  when user preferences are changed. Won't be needed anymore when
        * and if AbstractComposeView can work with HiltViewModel */
        (view as ComposeKeyboardView).disposeComposition()

        super.onStartInputView(info, restarting)

        info?.let {
            classInputType = info.inputType and InputType.TYPE_MASK_CLASS
            variationInputType = info.inputType and InputType.TYPE_MASK_VARIATION
        }

        val selectedText = (currentInputConnection.getSelectedText(0) ?: "").toString()
        val capsIndexes = mutableListOf<Int>()

        textBeforeCursor = (currentInputConnection?.getTextBeforeCursor(5000, 0) ?: "").toString()
        textAfterCursor = (currentInputConnection?.getTextAfterCursor(5000, 0) ?: "").toString()


        textSelection =
                TextSelection(
                    textBeforeCursor.length,
                    textBeforeCursor.length + selectedText.length,
                    selectedText
                )

        val text = textBeforeCursor + textSelection.text + textAfterCursor


        text.toCharArray().forEachIndexed { index, c ->
            if (c.isUpperCase())
                capsIndexes.add(index)
        }

        this.capsIndexes = capsIndexes


        textComposition = TextComposition(textBeforeCursor.length, textBeforeCursor.length, "")
        if (textSelection.length == 0 ) {
            setComposingRegion()
        } else finishComposingText()

        if (isAutoCaps.value &&
            (textBeforeCursor.trimEnd().endsWith(".") ||
                    textBeforeCursor.trimEnd().endsWith("?") ||
                    textBeforeCursor.trimEnd().endsWith("!")
                    )
            ) {
            isCaps.value = KeyboardCapsStatus.UPPER_CASE
        }

    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateInputView(): View {

        Log.d(TAG, "onCreateInputView")
        setBackgroundColorId()
        view = ComposeKeyboardView(this, backgroundColorId, keyboardProvider)

        window!!.window!!.decorView.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
//            ViewTreeSavedStateRegistryOwner.set(decorView, this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
        window!!.window!!.navigationBarColor = this.getColor(backgroundColorId)
        view.let {
            it.setViewTreeLifecycleOwner(this)
            it.setViewTreeViewModelStoreOwner(this)
//            ViewTreeSavedStateRegistryOwner.set(it, this)
            it.setViewTreeSavedStateRegistryOwner(this)
        }

        userPreferencesRepository = UserPreferencesRepositoryLocal(this.dataStore)


//        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
//        val setLanguage = prefs.getString(
//            this.getString(R.string.shared_prefs_set_language),
//            LANGUAGE_TAG_ENGLISH_AMERICAN
//        )
//        val dbBaseName = this.getString(R.string.db_base_name)
//        db = DictionaryDataHelper(this, "${dbBaseName}_${setLanguage}.sqlite")
        val userPreferencesRepository = UserPreferencesRepositoryLocal(application.dataStore)
        val languageSet = runBlocking{
            var value = ""
            userPreferencesRepository.getLanguage().map {
                value = it
            }
            value
        }

//        db = if (BuildConfig.DEBUG) {
//            DictionaryDataHelper(this, "dictionary.sqlite")
//        } else {
//            DictionaryDataHelper(this, "${DATABASE_NAME}_${languageSet}.sqlite")
//        }

        Log.d(TAG, "database name ${DATABASE_NAME}_${languageSet}.sqlite")
        db = DictionaryDataHelper(this, "${DATABASE_NAME}_${languageSet}.sqlite")

//        db = DictionaryDataHelper(this, "dictionary.sqlite")
        db.writableDatabase.enableWriteAheadLogging()//db.readableDatabase
        db.writableDatabase.execSQL("PRAGMA synchronous = NORMAL")

        lifecycleScope.launch {
            isManual.value = userPreferencesRepository.isStartWithManualEnabled().getOrNull()!!
            _doubleSpaceCharState.value = userPreferencesRepository.getDoubleSpaceCharacter().getOrNull()!!
            isAutoCaps.value = userPreferencesRepository.isAutoCapsEnabled().getOrNull()!!


            var getFrequencyTime = measureTimeMillis {
                meanFrequency = db.getMeanFrequency()
                wordsMaxLength = db.getMaxLength()
            }
        }

        return view
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        Log.d(TAG, "onFinishInputView")
        super.onFinishInputView(finishingInput)

        Log.d(TAG, "$textBeforeCursor${textSelection.text}$textAfterCursor")

        if (!inputIsPassword()) {
            val admissibleChars = mutableListOf<Char>()

            (textBeforeCursor + textSelection.text + textAfterCursor)
                .toCharArray()
                .toMutableList()
                .forEach{char ->
                    if (char.toString().matches(WORDS_SPACE_REGEX)) {
                        admissibleChars.add(char)
                    }
                }

            val writtenWords = admissibleChars.joinToString("").split(" ")

            writtenWords.forEach {
                val word =
                    Word(
                        it,
                        USER_WORDS_FLAG
                    )
                Log.d(TAG, "word = ${word.text}")
                db.upsert(word)
            }
        }

        currentWord = null
        currentT9code = ""
        words.clear()

        currentInputConnection.finishComposingText()
        textComposition.reset()

        capsIndexes.clear()

        textBeforeCursor = ""
        textAfterCursor = ""


        Log.d(TAG, "onFinishInputView")
    }

    //Lifecylce Methods

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

    override fun onWindowShown() {
        val userPreferencesRepository = UserPreferencesRepositoryLocal(application.dataStore)
        val languageSet = runBlocking{
            var value = ""
            userPreferencesRepository.getLanguage().map {
                value = it
            }
            value
        }



//        db = if (BuildConfig.DEBUG) {
//            DictionaryDataHelper(this, "dictionary.sqlite")
//        } else {
//            DictionaryDataHelper(this, "${DATABASE_NAME}_${languageSet}.sqlite")
//        }

        db = DictionaryDataHelper(this, "${DATABASE_NAME}_${languageSet}.sqlite")

//        db = DictionaryDataHelper(this, "dictionary.sqlite")
        db.writableDatabase.enableWriteAheadLogging()//db.readableDatabase
        db.writableDatabase.execSQL("PRAGMA synchronous = NORMAL")
        super.onWindowShown()
    }

    override fun onWindowHidden() {
        db.close()
        super.onWindowHidden()
    }

    override fun onUpdateSelection(
        oldSelStart: Int,
        oldSelEnd: Int,
        newSelStart: Int,
        newSelEnd: Int,
        candidatesStart: Int,
        candidatesEnd: Int
    ) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)

        Log.d(TAG, "onUpdateSelection")
        Log.d(TAG, "newSelStart: $newSelStart | newSelEnd: $newSelEnd")
        Log.d(TAG, "oldSelStart: $oldSelStart | oldSelEnd: $oldSelEnd")

        /* The if check is due to the case when the user is selecting a text and then moves the end
        * cursor before the start cursor. When this happen onUpdateSelection is being recalled with
        * the two cursors in the correct order */
        if ( newSelStart <= newSelEnd) {

            textBeforeCursor =
                (currentInputConnection?.getTextBeforeCursor(5000, 0) ?: "").toString()
            textAfterCursor =
                (currentInputConnection?.getTextAfterCursor(5000, 0) ?: "").toString()
            textSelection
                .setSelection(
                    newSelStart,
                    (currentInputConnection?.getSelectedText(0) ?: "").toString()
                )

            if (textSelection.text.isNotEmpty()) {
                finishComposingText()
            } else if (!isManual.value)
                setComposingRegion()

        }

        if (isAutoCaps.value &&
            (textBeforeCursor.trimEnd().endsWith(".") ||
                    textBeforeCursor.trimEnd().endsWith("?") ||
                    textBeforeCursor.trimEnd().endsWith("!")
                    )
        ) {
            isCaps.value = KeyboardCapsStatus.UPPER_CASE
        }

        _textLengthState.intValue =
            textBeforeCursor.length + textSelection.length + textAfterCursor.length

    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateExtractTextView(): View {

        val inflater =
            super.onCreateExtractTextView()// returns standard com.android.internal.R.layout.input_method_extract_view

        val res: Resources = Resources.getSystem() // system resources

        val id_inputExtractEditText =
            res.getIdentifier("inputExtractEditText", "id", "android") // ExtractEditText
        val id_inputExtractAccessories =
            res.getIdentifier("inputExtractAccessories", "id", "android") // FrameLayout
        val id_inputExtractAction =
            res.getIdentifier("inputExtractAction", "id", "android") // ExtractButton

        val inputExtractEditText = inflater.findViewById<ExtractEditText>(id_inputExtractEditText)
        val inputExtractAccessories = inflater.findViewById<FrameLayout>(id_inputExtractAccessories)
        val inputExtractAction: ImageButton = inflater.findViewById(id_inputExtractAction)

        inputExtractAction.visibility = View.INVISIBLE
        val displayMetrics = DisplayMetrics()
        val window = getSystemService(Context.WINDOW_SERVICE) as WindowManager

        window.defaultDisplay.getMetrics(displayMetrics)
        val padding =
            (displayMetrics.widthPixels * (sqrt(2.0) - 1) / (2 * displayMetrics.density)).toInt() + 5

        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(padding, 0, 0, 0)

        inputExtractEditText.layoutParams = layoutParams
        inputExtractEditText.gravity = Gravity.BOTTOM
        inputExtractEditText.setSingleLine()

        return inflater


    }

    override fun onEvaluateFullscreenMode(): Boolean {
        return if ( packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH) )
            true
        else
            super.onEvaluateFullscreenMode()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    fun setBackgroundColorId() {
        val nightModeFlags: Int = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        backgroundColorId = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> android.R.color.system_neutral1_900
            else -> android.R.color.system_neutral2_50
        }
    }

    private fun getWordTextBeforeCursor(): String{
        return textBeforeCursor.substringAfterLastNotMatching(WORDS_REGEX)
    }

    private fun getWordTextAfterCursor(): String{
        return textAfterCursor.substringBeforeFirstNotMatching(WORDS_REGEX)
    }

    private fun updateCurrentWord(newCode: Char?){

        var attempt = 1

        val wordTextBeforeCursor = getWordTextBeforeCursor()
        val wordTextAfterCursor = getWordTextAfterCursor()
        currentT9code =
            Word.getNumberDigitsCode(wordTextBeforeCursor) + (newCode?:"") +
                    Word.getNumberDigitsCode(wordTextAfterCursor)

        words = db.getWordsByCode(currentT9code)
        words.forEach {
            Log.d(TAG, "word = ${it.text}")
        }


        if(newCode == null){

            currentWord =
                words
                    .filter { word: Word ->
                        word.text.compareTo(
                            wordTextBeforeCursor + wordTextAfterCursor,
                            ignoreCase = true) == 0
                    }
                    .firstOrNull()?: Word(
                wordTextBeforeCursor + wordTextAfterCursor
                )

        } else {

            if (words.isNotEmpty()){
                currentWord = words.first()
            } else if (currentT9code.isNotEmpty()){

                //TODO: improve this fetch
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

    private fun getCapsIndexesOfCurrentWord(): List<Int>{

        val capsIndexesOfCurrentWord: List<Int>

        capsIndexesOfCurrentWord =
            capsIndexes
                .filter {
                    it >= textSelection.startIndex - getWordTextBeforeCursor().length
                    && it <= textSelection.startIndex + getWordTextAfterCursor().length + 1
                }
                .map { it - (textSelection.startIndex - getWordTextBeforeCursor().length) }

        return capsIndexesOfCurrentWord

    }

    fun deleteChar(){
        if (textSelection.text.isEmpty() && textBeforeCursor.isNotEmpty()) {
            capsIndexes.remove(textSelection.startIndex - 1)
        } else if (textSelection.text.isNotEmpty()) {
            capsIndexes.removeAll(
                textSelection.startIndex until textSelection.endIndex
            )
        }

        val downEvent = KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)
        val upEvent = KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL)
        currentInputConnection?.sendKeyEvent(downEvent)
        currentInputConnection?.sendKeyEvent(upEvent)

    }



    fun newLine(){
        commitText("\n")
    }

    private fun addCharToCurrentWord(code: Int) {

        val digit = code.toChar()

        updateCurrentWord(digit)

        val currentWordCharArray = currentWord!!.text.toCharArray()

        val wordTextBeforeCursor = getWordTextBeforeCursor()

        if (isCaps.value == KeyboardCapsStatus.UPPER_CASE || isCaps.value == KeyboardCapsStatus.CAPS_LOCK) {

            capsIndexes.add(textSelection.startIndex)
            currentWordCharArray[wordTextBeforeCursor.length] =
                currentWordCharArray[wordTextBeforeCursor.length].uppercaseChar()

            if (isCaps.value == KeyboardCapsStatus.UPPER_CASE) {
                isCaps.value = KeyboardCapsStatus.LOWER_CASE
            }

        }


        var capsIndexesOfCurrentWord = getCapsIndexesOfCurrentWord()
        capsIndexesOfCurrentWord.forEach {

            currentWordCharArray[it] = currentWordCharArray[it].uppercaseChar()

        }

        setComposingText(String(currentWordCharArray))

    }

    private fun addCodeToCurrentText(inputCode: Int) {

        val iToByteArray =
            String(intArrayOf(inputCode), 0, 1).toByteArray(Charsets.UTF_16)
        val code = String(iToByteArray, Charsets.UTF_16).toCharArray()

        setComposingText(String(code))
    }

    fun writeSpecificChar(char: String) {
        commitText(char)
        if (isCaps.value == KeyboardCapsStatus.UPPER_CASE) {
            isCaps.value = KeyboardCapsStatus.LOWER_CASE

        }
    }

    fun keyClick(codes: IntArray) {

            Log.d(TAG, "keyClick")
            var digitCode = codes.last()

            if (digitCode != null) addCharToCurrentWord(digitCode)
    }

    fun addCharToCurrentText(codes: IntArray, keyId: Int) {

        if (lastKeyId == keyId &&
            System.currentTimeMillis() - keyTimer < LONG_PRESSURE_TIME_MILLIS) {

            if (keyCodesIndex < codes.size - 1) {
                keyCodesIndex += 1
            } else {
                keyCodesIndex = 0
            }

        } else {
            finishComposingText()
            if (isCaps.value != KeyboardCapsStatus.LOWER_CASE) {
                capsIndexes.add(textSelection.startIndex)
            }
            keyCodesIndex = 0

        }

        addCodeToCurrentText(codes[keyCodesIndex])
        lastKeyId = keyId
        keyTimer = System.currentTimeMillis()

    }

    fun spaceClick() {
        val code = ASCII_CODE_SPACE.toChar()
        commitText(code.toString())
    }

    fun doubleSpaceClick() {
        commitText("${_doubleSpaceCharState.value.value}${ASCII_CODE_SPACE.toChar()}")
    }

    fun swapClick() {
            var currentWordCharArray: CharArray

            var currentIndex = words.indexOf(currentWord)


            try {

                currentWord = words[currentIndex + 1]
                Log.d(TAG, "new currentWord = ${currentWord!!.text}")
                currentWordCharArray = currentWord!!.text.toCharArray()

                getCapsIndexesOfCurrentWord().forEach {

                    currentWordCharArray[it] =
                        currentWordCharArray[it].uppercaseChar()

                }

                setComposingText(String(currentWordCharArray))

            } catch (e: IndexOutOfBoundsException) {

                Log.d(TAG, "index out of bounds")

                enterManualMode()

            }


    }

    fun emojiClick(emojiViewItem: EmojiViewItem) {
        val emoji : String = emojiViewItem.emoji
        commitText(emoji)
    }

    fun enterManualMode() {
        if (isCaps.value == KeyboardCapsStatus.UPPER_CASE)
            isCaps.value = KeyboardCapsStatus.CAPS_LOCK
        wasManual = isManual.value
        isManual.value = true
        finishComposingText()
    }

    fun exitManualMode() {
        if(!wasManual) {
            isManual.value = false
        }
        wasManual = false
    }

    private fun commitText(s: String) {
        finishComposingText()
        textComposition.reset(textBeforeCursor.length + s.length)
        if (isCaps.value != KeyboardCapsStatus.LOWER_CASE) {
            capsIndexes.add(textSelection.startIndex)
        }
        currentInputConnection?.commitText(s, 1)
    }

    private fun setComposingRegion() {
        val composingStartIndex: Int
        val composingEndIndex: Int

        val wordTextBeforeCursor: String = getWordTextBeforeCursor()
        val wordTextAfterCursor: String = getWordTextAfterCursor()

        if (wordTextBeforeCursor.isNotEmpty())
            composingStartIndex = textBeforeCursor.lastIndexOf(wordTextBeforeCursor)
        else
            composingStartIndex = textBeforeCursor.length

        composingEndIndex =
            composingStartIndex + wordTextBeforeCursor.length + wordTextAfterCursor.length

        updateCurrentWord(null)

        textComposition.setRegion(composingStartIndex, wordTextBeforeCursor + wordTextAfterCursor)
        currentInputConnection.setComposingRegion(composingStartIndex, composingEndIndex)
    }

    private fun finishComposingText() {
        textComposition.reset(textBeforeCursor.length)
        currentInputConnection.finishComposingText()
    }

    private fun setComposingText(newText: String) {
        textComposition.setText(newText)
        currentInputConnection.setComposingText(newText, 1)
    }

    fun inputIsPassword() : Boolean {
        return variationInputType == InputType.TYPE_TEXT_VARIATION_PASSWORD
                || variationInputType == InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                || variationInputType == InputType.TYPE_TEXT_VARIATION_WEB_PASSWORD
    }

    companion object {
        const val LONG_PRESSURE_TIME_MILLIS = 500L
        val WORDS_REGEX = WORDS_REGEX_STRING.toRegex()
        val WORDS_SPACE_REGEX = WORDS_SPACE_REGEX_STRING.toRegex()
    }


}