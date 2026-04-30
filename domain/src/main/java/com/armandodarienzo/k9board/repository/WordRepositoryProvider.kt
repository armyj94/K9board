package com.armandodarienzo.k9board.repository

interface WordRepositoryProvider {
    fun getForLanguage(languageTag: String): WordRepository
}
