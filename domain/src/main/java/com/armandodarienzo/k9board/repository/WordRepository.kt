package com.armandodarienzo.k9board.repository

import com.armandodarienzo.k9board.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    suspend fun getWordsByCode(code: String): List<Word>
    suspend fun getPlaceholderWordsByCode(code: String, queryDepth: Int): List<Word>
    suspend fun getWordsByFlag(flag: String): List<Word>
    fun getUserWords(): Flow<List<Word>>
    suspend fun getMeanFrequency(): Int
    suspend fun getMaxLength(): Int
    suspend fun upsert(word: Word)
    suspend fun delete(wordText: String)
    suspend fun saveUserWord(wordText: String)
}
