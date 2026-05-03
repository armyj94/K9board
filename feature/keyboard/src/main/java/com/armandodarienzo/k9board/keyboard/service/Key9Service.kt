package com.armandodarienzo.k9board.keyboard.service

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
import android.view.KeyEvent
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.annotation.RequiresApi
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
import com.armandodarienzo.k9board.keyboard.KeyboardEffect
import com.armandodarienzo.k9board.keyboard.KeyboardIntent
import com.armandodarienzo.k9board.keyboard.KeyboardFactory
import com.armandodarienzo.k9board.keyboard.KeyboardViewModel
import com.armandodarienzo.k9board.keyboard.ui.ComposeKeyboardView
import com.armandodarienzo.k9board.shared.KEYBOARD_MIN_SIZE
import com.armandodarienzo.k9board.shared.KEYBOARD_SIZE_FACTOR_WATCH
import com.armandodarienzo.k9board.shared.model.KeyboardSize
import com.armandodarienzo.k9board.shared.model.SupportedLanguageTag
import com.armandodarienzo.k9board.shared.repository.UserPreferencesRepositoryLocal
import com.armandodarienzo.k9board.shared.repository.dataStore
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.math.sqrt

@AndroidEntryPoint
open class Key9Service : InputMethodService(), LifecycleOwner, ViewModelStoreOwner,
    SavedStateRegistryOwner {

    @Inject lateinit var keyboardFactory: KeyboardFactory
    @Inject lateinit var keyboardViewModel: KeyboardViewModel

    private val TAG = Key9Service::class.java.simpleName

    private val lifecycleRegistry = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegistry
    private val savedStateRegistryController = SavedStateRegistryController.create(this)
    override val savedStateRegistry = savedStateRegistryController.savedStateRegistry
    override val viewModelStore: ViewModelStore = ViewModelStore()

    private var backgroundColorId: Int = 0
    private lateinit var view: View

    override fun onCreate() {
        super.onCreate()
        savedStateRegistryController.performRestore(null)
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_RESUME)
        lifecycleScope.launch {
            keyboardViewModel.effects.collect { executeEffect(it) }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        lifecycleRegistry.handleLifecycleEvent(Lifecycle.Event.ON_DESTROY)
        keyboardViewModel.clear()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateInputView(): View {
        Log.d(TAG, "onCreateInputView")
        setBackgroundColorId()
        view = ComposeKeyboardView(this, keyboardViewModel, keyboardFactory)

        window!!.window!!.decorView.let { decorView ->
            decorView.setViewTreeLifecycleOwner(this)
            decorView.setViewTreeViewModelStoreOwner(this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
        window!!.window!!.navigationBarColor = this.getColor(backgroundColorId)
        view.setViewTreeLifecycleOwner(this)
        view.setViewTreeViewModelStoreOwner(this)
        view.setViewTreeSavedStateRegistryOwner(this)

        lifecycleScope.launch { loadAndDispatchPreferences() }
        return view
    }

    override fun onStartInputView(info: EditorInfo?, restarting: Boolean) {
        // When the user taps a different field without the keyboard hiding, the IME reuses the
        // existing view without recreating it. The Compose composition can get stale because it
        // was never torn down. Disposing it here forces a clean rebuild on the next layout pass,
        // preventing blank keyboards and effects leaking from the previous input connection.
        (view as ComposeKeyboardView).disposeComposition()
        super.onStartInputView(info, restarting)

        val textBefore = (currentInputConnection?.getTextBeforeCursor(5000, 0) ?: "").toString()
        val textAfter = (currentInputConnection?.getTextAfterCursor(5000, 0) ?: "").toString()
        val selectedText = (currentInputConnection?.getSelectedText(0) ?: "").toString()

        keyboardViewModel.processIntent(
            KeyboardIntent.InputStarted(
                textBefore = textBefore,
                textAfter = textAfter,
                selectedText = selectedText,
                classInputType = info?.inputType?.and(InputType.TYPE_MASK_CLASS) ?: 0,
                variationInputType = info?.inputType?.and(InputType.TYPE_MASK_VARIATION) ?: 0,
                imeActionId = info?.imeOptions?.and(EditorInfo.IME_MASK_ACTION) ?: 0,
            )
        )
    }

    override fun onFinishInputView(finishingInput: Boolean) {
        Log.d(TAG, "onFinishInputView")
        super.onFinishInputView(finishingInput)

        val textBefore = (currentInputConnection?.getTextBeforeCursor(5000, 0) ?: "").toString()
        val textAfter = (currentInputConnection?.getTextAfterCursor(5000, 0) ?: "").toString()
        val selectedText = (currentInputConnection?.getSelectedText(0) ?: "").toString()
        keyboardViewModel.processIntent(KeyboardIntent.InputFinished(textBefore, textAfter, selectedText))
    }

    override fun onWindowShown() {
        super.onWindowShown()
        lifecycleScope.launch { loadAndDispatchPreferences() }
    }

    override fun onWindowHidden() {
        super.onWindowHidden()
    }

    override fun onUpdateSelection(
        oldSelStart: Int, oldSelEnd: Int,
        newSelStart: Int, newSelEnd: Int,
        candidatesStart: Int, candidatesEnd: Int,
    ) {
        super.onUpdateSelection(oldSelStart, oldSelEnd, newSelStart, newSelEnd, candidatesStart, candidatesEnd)
        if (newSelStart <= newSelEnd) {
            val textBefore = (currentInputConnection?.getTextBeforeCursor(5000, 0) ?: "").toString()
            val textAfter = (currentInputConnection?.getTextAfterCursor(5000, 0) ?: "").toString()
            val selectedText = (currentInputConnection?.getSelectedText(0) ?: "").toString()
            keyboardViewModel.processIntent(
                KeyboardIntent.SelectionUpdated(
                    textBefore = textBefore,
                    textAfter = textAfter,
                    selectedText = selectedText,
                    newSelStart = newSelStart,
                )
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onCreateExtractTextView(): View {
        val inflater = super.onCreateExtractTextView()
        val res = Resources.getSystem()
        val inputExtractEditText = inflater.findViewById<ExtractEditText>(
            res.getIdentifier("inputExtractEditText", "id", "android")
        )
        val inputExtractAction: ImageButton = inflater.findViewById(
            res.getIdentifier("inputExtractAction", "id", "android")
        )
        inputExtractAction.visibility = View.INVISIBLE

        val displayMetrics = DisplayMetrics()
        (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay.getMetrics(displayMetrics)
        val padding = (displayMetrics.widthPixels * (sqrt(2.0) - 1) / (2 * displayMetrics.density)).toInt() + 5
        val layoutParams = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        layoutParams.setMargins(padding, 0, 0, 0)
        inputExtractEditText.layoutParams = layoutParams
        inputExtractEditText.gravity = android.view.Gravity.BOTTOM
        inputExtractEditText.setSingleLine()
        return inflater
    }

    override fun onEvaluateFullscreenMode(): Boolean =
        if (packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)) true
        else super.onEvaluateFullscreenMode()

    @RequiresApi(Build.VERSION_CODES.S)
    private fun setBackgroundColorId() {
        backgroundColorId = when (resources.configuration.uiMode and Configuration.UI_MODE_NIGHT_MASK) {
            Configuration.UI_MODE_NIGHT_YES -> android.R.color.system_neutral1_900
            else -> android.R.color.system_neutral2_50
        }
    }

    private suspend fun loadAndDispatchPreferences() {
        val repo = UserPreferencesRepositoryLocal(application.dataStore)
        val languageSet = repo.getLanguage().getOrDefault(SupportedLanguageTag.AMERICAN.value)
        val themeSet = repo.getTheme().getOrDefault("")
        val isManualDefault = repo.isStartWithManualEnabled().getOrNull() ?: false
        val doubleSpaceChar = repo.getDoubleSpaceCharacter().getOrNull()
            ?: com.armandodarienzo.k9board.shared.model.DoubleSpaceCharacter.NONE
        val isAutoCaps = repo.isAutoCapsEnabled().getOrNull() ?: false
        val hapticFeedback = repo.isHapticFeedbackEnabled().getOrNull() ?: false
        val keyboardSizeFactor = if (packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)) {
            KEYBOARD_SIZE_FACTOR_WATCH
        } else {
            repo.getKeyboardSize().getOrNull()?.factor ?: KeyboardSize.MEDIUM.factor
        }
        val screenHeightDp = resources.configuration.screenHeightDp
        val keyboardSize = if (packageManager.hasSystemFeature(PackageManager.FEATURE_WATCH)) {
            (screenHeightDp * keyboardSizeFactor).toInt()
        } else {
            maxOf(KEYBOARD_MIN_SIZE, (screenHeightDp * keyboardSizeFactor).toInt())
        }

        keyboardViewModel.processIntent(
            KeyboardIntent.PreferencesLoaded(
                languageSet = languageSet,
                themeSet = themeSet,
                keyboardSize = keyboardSize,
                hapticFeedback = hapticFeedback,
                backgroundColorId = backgroundColorId,
                isManualDefault = isManualDefault,
                doubleSpaceChar = doubleSpaceChar,
                isAutoCaps = isAutoCaps,
            )
        )
    }

    private fun executeEffect(effect: KeyboardEffect) {
        when (effect) {
            is KeyboardEffect.CommitText -> {
                currentInputConnection?.finishComposingText()
                currentInputConnection?.commitText(effect.text, 1)
            }
            is KeyboardEffect.SetComposingText ->
                currentInputConnection?.setComposingText(effect.text, 1)
            is KeyboardEffect.FinishComposing ->
                currentInputConnection?.finishComposingText()
            is KeyboardEffect.SetComposingRegion ->
                currentInputConnection?.setComposingRegion(effect.start, effect.end)
            is KeyboardEffect.SendKeyEvent -> {
                currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, effect.keyCode))
                currentInputConnection?.sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, effect.keyCode))
            }
            is KeyboardEffect.PerformEditorAction ->
                currentInputConnection?.performEditorAction(effect.actionId)
        }
    }
}
