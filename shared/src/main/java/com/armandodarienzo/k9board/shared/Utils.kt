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

fun packName(tag: String) : String {
    return "${ASSET_PACKS_BASE_NAME}_${tag.replace("-", "_")}"
}

fun getDatabaseName(tag: String) : String {
    return "${DATABASE_NAME}_${tag}.sqlite"
}

fun String.substringAfterLastNotMatching(regex: Regex): String {
    var index = 0
    for (i in indices) {
        if (!regex.matches(this.reversed()[i].toString())) {
            index = this.length - i
            break // Stop after finding the first non-matching character
        }
    }
    return this.substring(index)
}

fun String.substringBeforeFirstNotMatching(regex: Regex): String {
    //val index = regex.find(this)?.range?.first ?: this.length
    var index = this.length
    for (i in indices) {
        if (!regex.matches(this[i].toString())) {
            index = i
            break // Stop after finding the first non-matching character
        }
    }
    return this.substring(0, index)
}