package com.armandodarienzo.k9board.shared.model

/* This class is used to keep track of the selection in the text or cursor position. This is because
*  as stated on Android documentation
* ( https://developer.android.com/reference/android/view/inputmethod/InputConnection ) cursor is
* a zero-length selection */
class TextSelection(
    startIndex: Int,
    endIndex: Int,
    text: String
) {
    var startIndex = startIndex
        private set

    var endIndex = endIndex
        private set

    var text = text
        private set

    val length: Int = endIndex - startIndex

    fun setSelection(startIndex: Int, newText: String) {
        this.startIndex = startIndex
        text = newText
        endIndex = startIndex + text.length
    }


}