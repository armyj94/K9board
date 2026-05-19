package com.armandodarienzo.k9board.model

import com.google.gson.annotations.SerializedName

var meanWordsFrequency = 50

data class Word(
    @SerializedName("text") val text: String,
    @SerializedName("frequency") val frequency: Int?,
    @SerializedName("flags") val flags: String?,
    @SerializedName("originalFrequency") val originalFrequency: Int?,
    @SerializedName("possiblyOffensive") val possiblyOffensive: String,
    @SerializedName("t9Code") val t9Code: String?,
) {

    constructor(text: String) : this(
        text,
        meanWordsFrequency, null,
        meanWordsFrequency, "false",
        getNumberDigitsCode(text)
    )

    constructor(text: String, flags: String) : this(
        text,
        meanWordsFrequency, flags,
        meanWordsFrequency, "false",
        getNumberDigitsCode(text)
    )

    fun getNumberDigitsCode(): String = t9Code ?: getNumberDigitsCode(text)

    companion object {

        fun getNumberDigitsCode(word: String): String {

            var result = ""

            for (char in word.toCharArray()) {

                when (char) {

                    ',', '.', '\'', ':', ';', '"', '_', '?', '¿', '!', '¡', '+', '-', '=', '(', ')', '$', '@', '&', '\\', '#', '€', '*', '/', '₽', '£', '<', '>', '%', '1' -> result += "1"
                    'A', 'B', 'C', 'a', 'b', 'c', 'À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Ă', 'Æ', 'Ç', 'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ă', 'Ą', 'ą', 'Ā', 'ā', 'ç', 'Č', 'č', 'Ć', 'ć', '2',
                    'А', 'а', 'Б', 'б', 'В', 'в', 'Г', 'г' -> result += "2"
                    'D', 'E', 'F', 'd', 'e', 'f', 'Ð', 'È', 'É', 'Ê', 'Ë', 'Ě', 'ð', 'è', 'é', 'ê', 'ë', 'Ď', 'ď', 'Đ', 'đ', 'Ě', 'ě', 'Ę', 'ę', 'Ė', 'ė', '3',
                    'Д', 'д', 'Е', 'е', 'Ж', 'ж', 'З', 'з', 'Ё', 'ё' -> result += "3"
                    'G', 'H', 'I', 'g', 'h', 'i', 'Ì', 'Í', 'Î', 'Ï', 'ì', 'í', 'î', 'ï', 'ı', 'Ǐ', 'ǐ', 'Į', 'į', '4',
                    'И', 'и', 'Й', 'й', 'К', 'к', 'Л', 'л' -> result += "4"
                    'J', 'K', 'L', 'j', 'k', 'l', 'Ľ', 'ľ', 'Ł', 'ł', '5',
                    'М', 'м', 'Н', 'н', 'О', 'о', 'П', 'п' -> result += "5"
                    'M', 'N', 'O', 'm', 'n', 'o', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', 'Ø', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', 'ø', 'Œ', 'œ', 'Ō', 'ō', 'Ő', 'ő', 'Ň', 'ň', 'Ń', 'ń', '6',
                    'Р', 'р', 'С', 'с', 'Т', 'т', 'У', 'у' -> result += "6"
                    'P', 'Q', 'R', 'S', 'Š', 'p', 'q', 'r', 's', 'š', 'Ř', 'ř', 'Ş', 'ş', 'Ś', 'ś', 'Ș', 'ș', '7',
                    'Ф', 'ф', 'Х', 'х', 'Ц', 'ц', 'Ч', 'ч' -> result += "7"
                    'T', 'U', 'V', 't', 'u', 'v', 'Ù', 'Ú', 'Û', 'Ü', 'Ů', 'Ű', 'ù', 'ú', 'û', 'ü', 'ů', 'ű', 'Ų', 'ų', 'Ū', 'ū', 'Ţ', 'ţ', 'Ť', 'ť', 'Ț', 'ț', '8',
                    'Ш', 'ш', 'Щ', 'щ', 'Ъ', 'ъ', 'Ы', 'ы' -> result += "8"
                    'W', 'X', 'Y', 'Z', 'Ý', 'Ÿ', 'Ž', 'w', 'x', 'y', 'ý', 'ÿ', 'z', 'ž', 'Ż', 'ż', 'Ź', 'ź', '9',
                    'Ь', 'ь', 'Э', 'э', 'Ю', 'ю', 'Я', 'я' -> result += "9"
                    '0' -> result += "0"
                    else -> result += "%%"
                }
            }

            return result
        }
    }
}
