package com.armandodarienzo.k9board.shared

private const val TAG = "Utils"

fun codifyChars(text: String): MutableList<Int>{

    val noSpaceText = text.replace(" ", "", true)
    var codes: MutableList<Int> = mutableListOf()

    for(char in noSpaceText){
        codes.add(char.toInt())
    }

    return codes

}