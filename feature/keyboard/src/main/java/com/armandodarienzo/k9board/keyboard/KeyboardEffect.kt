package com.armandodarienzo.k9board.keyboard

import com.armandodarienzo.k9board.shared.ui.base.Reducer

sealed class KeyboardEffect : Reducer.SideEffect {
    data class CommitText(val text: String) : KeyboardEffect()
    data class SetComposingText(val text: String) : KeyboardEffect()
    data object FinishComposing : KeyboardEffect()
    data class SetComposingRegion(val start: Int, val end: Int) : KeyboardEffect()
    data class SendKeyEvent(val keyCode: Int) : KeyboardEffect()
    data class PerformEditorAction(val actionId: Int) : KeyboardEffect()
    // Finish previous composing span then start a new one — used in manual mode when switching keys
    data class FinishComposingAndStart(val text: String) : KeyboardEffect()
    data class FinishComposingAndDelete(val charsBefore: Int) : KeyboardEffect()
    data object DeleteSelection : KeyboardEffect()
}