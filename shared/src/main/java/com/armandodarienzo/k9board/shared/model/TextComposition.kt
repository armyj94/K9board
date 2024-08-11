package com.armandodarienzo.k9board.shared.model

class TextComposition(
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

    fun setText(newText: String) {
        text = newText
        endIndex = startIndex + text.length
    }

    fun setRegion(startIndex: Int, newText: String) {
        this.startIndex = startIndex
        setText(newText)
    }

    fun reset(startIndex: Int) {
        this.startIndex = startIndex
        this.endIndex = startIndex
        this.text = ""
    }

    fun reset() {
        reset(0)
    }
}