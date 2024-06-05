package com.armandodarienzo.k9board.shared.model

enum class SupportedLanguageTag(val value: String) {
    AMERICAN("en-US"),
    CZECH("cs-CZ"),
    DANISH("da-DK"),
    GERMAN("de-DE"),
    SPANISH("es-ES"),
    ENGLISH("en-GB"),
    FINNISH("fi-FI"),
    FRENCH("fr-FR"),
//    CROATIAN("hr-HR"),
//    HUNGARIAN("hu-HU"),
    ITALIAN("it-IT"),
//    LITHUANIAN("lt-LT"),
//    NORWEGIAN("nb-NO"),
    DUTCH("nl-NL"),
//    POLISH("pl-PL"),
    PORTUGUESE("pt-PT"),
    BRAZILIAN("pt-BR"),
//    ROMANIAN("ro-RO"),
//    RUSSIAN("ru-RU"),
//    SLOVENIAN("sl_SL"),
    SWEDISH("sv-SE");
//    TURKISH("tr_TR");

    companion object {
        private val map = SupportedLanguageTag.values().associateBy { it.value }
        infix fun from(tag: String) = map[tag]
    }
}