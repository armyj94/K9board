package com.armandodarienzo.k9board.keyboard.usecase

import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.repository.WordRepositoryProvider
import com.armandodarienzo.k9board.shared.USER_WORDS_FLAG
import com.armandodarienzo.k9board.shared.WORDS_SPACE_REGEX_STRING
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UpsertTypedWordsUseCase @Inject constructor(
    private val wordRepositoryProvider: WordRepositoryProvider,
) {
    private val wordsSpaceRegex = WORDS_SPACE_REGEX_STRING.toRegex()

    suspend operator fun invoke(text: String, languageTag: String, isPassword: Boolean) {
        if (isPassword || languageTag.isEmpty()) return
        withContext(Dispatchers.IO) {
            val repo = wordRepositoryProvider.getForLanguage(languageTag)
            val admissible = text.filter { it.toString().matches(wordsSpaceRegex) }
            admissible.split(" ").forEach { wordText ->
                if (wordText.isNotEmpty()) repo.upsert(Word(wordText, USER_WORDS_FLAG))
            }
        }
    }
}
