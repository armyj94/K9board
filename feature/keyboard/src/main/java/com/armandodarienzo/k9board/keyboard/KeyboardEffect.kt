package com.armandodarienzo.k9board.keyboard

sealed class KeyboardEffect {
    data class CommitText(val text: String) : KeyboardEffect()
    data class SetComposingText(val text: String) : KeyboardEffect()
    object FinishComposing : KeyboardEffect()
    data class SetComposingRegion(val start: Int, val end: Int) : KeyboardEffect()
    data class SendKeyEvent(val keyCode: Int) : KeyboardEffect()
    data class PerformEditorAction(val actionId: Int) : KeyboardEffect()
}
