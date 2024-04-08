package com.armandodarienzo.k9board.model

import com.google.gson.annotations.SerializedName

var meanWordsFrequency = 50

class Word(@SerializedName("text") val text: String,@SerializedName("frequency") var frequency: Int?, @SerializedName("flags") var flags: String?,
           @SerializedName("originalFrequency") var originalFrequency: Int?, @SerializedName("possiblyOffensive") var possiblyOffensive: String, @SerializedName("t9Code") var t9Code: String?) {

    constructor(text: String): this(text,
        meanWordsFrequency, null,
        meanWordsFrequency, "false" ,
        getNumberDigitsCode(
            text
        )
    )

    constructor(text: String, flags:String): this(text,
        meanWordsFrequency, flags,
        meanWordsFrequency, "false",
        getNumberDigitsCode(
            text
        )
    )



    fun getNumberDigitsCode(): String{

        return this.t9Code ?: getNumberDigitsCode(
            this.text
        )

    }

    fun setT9Code(){

        this.t9Code =
            getNumberDigitsCode(
                this.text
            )

    }

    companion object{

        fun getNumberDigitsCode(word: String): String{

            var result = ""

            for(char in word.toCharArray()){

                when(char){

                    ',', '.', '\'', ':', ';', '"', '_', '?', '¿', '!', '¡', '+', '-', '=', '(', ')', '$', '@', '&', '\\', '#', '€', '*', '/', '₽', '£', '<', '>', '%', '1'-> result += "1"
                    'A', 'B', 'C', 'a', 'b', 'c', 'À', 'Á', 'Â', 'Ã', 'Ä', 'Å', 'Ă', 'Æ', 'Ç', 'à', 'á', 'â', 'ã', 'ä', 'å', 'æ', 'ă', 'Ą', 'ą', 'Ā', 'ā', 'ç', 'Č', 'č', 'Ć', 'ć', '2',
                        'А' /*Russian A letter*/, 'а' /*Russian a letter*/, 'Б', 'б', 'В', 'в', 'Г', 'г'-> result += "2"
                    'D', 'E', 'F', 'd', 'e', 'f', 'Ð', 'È', 'É', 'Ê', 'Ë', 'Ě', 'ð', 'è', 'é', 'ê', 'ë', 'Ď', 'ď', 'Đ', 'đ', 'Ě', 'ě', 'Ę', 'ę', 'Ė', 'ė', '3',
                        'Д', 'д', 'Е' /*Russian E letter*/, 'е' /*Russian e letter*/, 'Ж', 'ж', 'З', 'з', 'Ё', 'ё'-> result += "3"
                    'G', 'H', 'I', 'g', 'h', 'i', 'Ì', 'Í', 'Î', 'Ï', 'ì', 'í', 'î', 'ï', 'ı', 'Ǐ', 'ǐ', 'Į', 'į', '4',
                        'И', 'и', 'Й', 'й', 'К' /*Russian K letter*/, 'к' /*Russian k letter*/, 'Л', 'л'-> result += "4"
                    'J', 'K', 'L', 'j', 'k', 'l', 'Ľ', 'ľ', 'Ł', 'ł', '5',
                        'М' /*Russian M letter*/,  'м' /*Russian m letter*/, 'Н' /*Russian H letter*/, 'н' /*Russian h letter*/, 'О' /*Russian O letter*/, 'о' /*Russian o letter*/, 'П', 'п'-> result += "5"
                    'M', 'N', 'O', 'm', 'n', 'o', 'Ñ', 'Ò', 'Ó', 'Ô', 'Õ', 'Ö', 'Ø', 'ñ', 'ò', 'ó', 'ô', 'õ', 'ö', 'ø', 'Œ', 'œ', 'Ō', 'ō', 'Ő', 'ő', 'Ň', 'ň', 'Ń', 'ń', '6',
                        'Р' /*Russian P letter*/, 'р' /*Russian p letter*/, 'С' /*Russian C letter*/, 'с' /*Russian c letter*/, 'Т' /*Russian T letter*/, 'т' /*Russian t letter*/, 'У' /*Russian Y letter*/, 'у' /*Russian y letter*/-> result += "6"
                    'P', 'Q', 'R', 'S', 'Š', 'p', 'q', 'r', 's', 'š', 'Ř', 'ř', 'Ş', 'ş', 'Ś', 'ś', 'Ș', 'ș', '7',
                        'Ф', 'ф', 'Х' /*Russian X letter*/, 'х' /*Russian x letter*/, 'Ц', 'ц', 'Ч', 'ч' -> result += "7"
                    'T', 'U', 'V', 't', 'u', 'v', 'Ù', 'Ú', 'Û', 'Ü', 'Ů', 'Ű', 'ù', 'ú', 'û', 'ü', 'ů', 'ű', 'Ų', 'ų', 'Ū', 'ū', 'Ţ', 'ţ', 'Ť', 'ť', 'Ț', 'ț', '8',
                        'Ш', 'ш', 'Щ', 'щ', 'Ъ', 'ъ', 'Ы', 'ы' -> result += "8"
                    'W', 'X', 'Y', 'Z', 'Ý', 'Ÿ', 'Ž', 'w', 'x', 'y', 'ý', 'ÿ', 'z', 'ž', 'Ż', 'ż', 'Ź', 'ź', '9',
                        'Ь' /*Russian B letter*/, 'ь' /*Russian b letter*/, 'Э', 'э', 'Ю', 'ю', 'Я', 'я' -> result += "9"
                    '0' -> result += "0"
                    else -> result += "%%"

                }

            }

            return result

        }

    }










}