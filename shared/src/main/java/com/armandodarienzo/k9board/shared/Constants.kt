package com.armandodarienzo.k9board.shared

/***** Keyboard UI Constant values *****/
const val KEYBOARD_MIN_SIZE = 180
const val KEYBOARD_SIZE_FACTOR_WATCH = 0.8
const val KEYBOARD_POPUP_MAX_COLUMNS = 5

const val KEY1_ID: Int = 1
const val KEY2_ID: Int = 2
const val KEY3_ID: Int = 3
const val KEY4_ID: Int = 4
const val KEY5_ID: Int = 5
const val KEY6_ID: Int = 6
const val KEY7_ID: Int = 7
const val KEY8_ID: Int = 8
const val KEY9_ID: Int = 9
const val KEY10_ID: Int = 10
const val KEYACTION_ID: Int = 103
const val KEYEMOJI_ID: Int = 104
const val KEYDELETE_ID: Int = 105
const val KEYSHIFT_ID: Int = 106
const val KEYSWAP_ID: Int = 107
const val KEYSPACE_ID: Int = 3000

const val ASCII_CODE_SPACE = 32
const val ASCII_CODE_1 = 49
const val ASCII_CODE_2 = 50
const val ASCII_CODE_3 = 51
const val ASCII_CODE_4 = 52
const val ASCII_CODE_5 = 53
const val ASCII_CODE_6 = 54
const val ASCII_CODE_7 = 55
const val ASCII_CODE_8 = 56
const val ASCII_CODE_9 = 57

const val KEY1_TEXT = ",.!?"

const val KEY2_TEXT_LATIN = "abc"
const val KEY3_TEXT_LATIN = "def"
const val KEY4_TEXT_LATIN = "ghi"
const val KEY5_TEXT_LATIN = "jkl"
const val KEY6_TEXT_LATIN = "mno"
const val KEY7_TEXT_LATIN = "pqrs"
const val KEY8_TEXT_LATIN = "tuv"
const val KEY9_TEXT_LATIN = "wxyz"


object Key2SpecialChars {
    val VALUES = listOf(
        "á", "à", "â", "ä", "ã", "å", "ą", "æ", "ć", "č", "ç"
    )
}

object Key3SpecialChars {
    val VALUES = listOf(
        "ď", "đ", "ð", "é", "è", "ė", "ê", "ë", "ě", "ę"
    )
}

object Key4SpecialChars {
    val VALUES = listOf(
        "ı", "í", "ì", "İ", "î", "ï", "į"
    )
}

object Key5SpecialChars {
    val VALUES = listOf(
        "ľ", "ł"
    )
}

object Key6SpecialChars {
    val VALUES = listOf(
        "ń", "ň", "ñ", "ó", "ò", "ô", "ö", "ō", "õ", "ő", "ø", "œ"
    )
}

object Key7SpecialChars {
    val VALUES = listOf(
        "ř", "ś", "š", "ş", "ș"
    )
}

object Key8SpecialChars {
    val VALUES = listOf(
        "ť", "ţ", "ț", "ú", "ù", "û", "ü", "ū", "ů", "ų", "ű"
    )
}

object Key9SpecialChars {
    val VALUES = listOf(
        "ý", "ÿ", "ź", "ż", "ž"
    )
}

object NumpadKey10SpecialChars {
    val VALUES = listOf(
        "+", "-", "*", "/", "=", "#"
    )
}



/***** Keyboard Colors Constant values *****/
const val THEME_DYNAMIC = "dynamic"
const val THEME_MATERIAL_YOU = "material_you"

/***** Database *****/

const val DATABASE_NAME = "dictionary"

const val ASSET_PACKS_BASE_NAME = "language"
const val LANGUAGE_TAG_ENGLISH_AMERICAN = "en-US"

const val USER_WORDS_FLAG = "hand-added"


/***** Shared Prefs *****/

const val USER_PREFERENCES_NAME = "user_preferences"

const val SHARED_PREFS_SET_LANGUAGE = "shared_prefs_set_language"
const val SHARED_PREFS_SET_THEME = "shared_prefs_set_theme"
const val SHARED_PREFS_HAPTIC_FEEDBACK = "user_prefs_haptic_feedback"
const val SHARED_PREFS_KEYBOARD_SIZE = "user_prefs_keys_size"
const val SHARED_PREFS_DOUBLE_SPACE_CHARACTER = "user_prefs_double_space_character"
const val SHARED_PREFS_START_MANUAL = "user_prefs_start_manual"
const val SHARED_PREFS_FIRST_EXECUTION = "shared_prefs_first_execution"
const val SHARED_PREFS_CURRENT_LANGUAGE = "shared_prefs_current_language"
const val SHARED_PREFS_LANGUAGE_ENABLED = "shared_prefs_language_enabled"
const val SHARED_PREFS_RECENT_EMOJIS = "shared_prefs_recent_emojis"


/***** Functional values *****/
const val WORDS_REGEX_STRING =
    "[',.\\':;\"\\?¿!¡+\\-=()\$@&\\\\#€*/₽£<>%0-9A-Za-zÀ-ÖØ-öø-ÿŒœŌōŐőŇňŃńА-Яа-яЁёЬьЭэЮюЯя]"
const val WORDS_SPACE_REGEX_STRING =
    "[',.\\':;\"\\?¿!¡+\\-=()\$@&\\\\#€*/₽£<>%0-9A-Za-zÀ-ÖØ-öø-ÿŒœŌōŐőŇňŃńА-Яа-яЁёЬьЭэЮюЯя ]"

