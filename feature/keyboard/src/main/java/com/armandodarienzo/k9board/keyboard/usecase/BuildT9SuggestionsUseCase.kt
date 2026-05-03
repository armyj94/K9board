package com.armandodarienzo.k9board.keyboard.usecase

import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.repository.WordRepositoryProvider
import javax.inject.Inject

class BuildT9SuggestionsUseCase @Inject constructor(
    private val wordRepositoryProvider: WordRepositoryProvider,
) {
    suspend operator fun invoke(t9code: String, languageTag: String, maxLength: Int): List<Word> {
        if (t9code.isEmpty() || languageTag.isEmpty()) return emptyList()
        val repo = wordRepositoryProvider.getForLanguage(languageTag)
        val words = repo.getWordsByCode(t9code).toMutableList()

        if (words.isEmpty() && t9code.length > 2) {
            var attempt = 1
            while (words.isEmpty() && t9code.length + attempt <= maxLength) {
                words.addAll(repo.getPlaceholderWordsByCode(t9code, attempt))
                attempt++
            }
        }
        return words
    }
}
