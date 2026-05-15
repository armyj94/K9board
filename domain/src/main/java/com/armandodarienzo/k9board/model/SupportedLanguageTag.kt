package com.armandodarienzo.k9board.model

enum class SupportedLanguageTag(val value: String) {
    AMERICAN("en-US"),
    CZECH("cs-CZ"),
    DANISH("da-DK"),
    GERMAN("de-DE"),
    SPANISH("es-ES"),
    ENGLISH("en-GB"),
    FINNISH("fi-FI"),
    FRENCH("fr-FR"),
    ITALIAN("it-IT"),
    DUTCH("nl-NL"),
    PORTUGUESE("pt-PT"),
    BRAZILIAN("pt-BR"),
    SWEDISH("sv-SE");

    companion object {
        private val map = values().associateBy { it.value }
        infix fun from(tag: String) = map[tag]
    }
}