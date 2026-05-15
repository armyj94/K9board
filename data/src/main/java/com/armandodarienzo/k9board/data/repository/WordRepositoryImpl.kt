package com.armandodarienzo.k9board.data.repository

import com.armandodarienzo.k9board.data.database.WordDatabase
import com.armandodarienzo.k9board.data.mapper.toDomain
import com.armandodarienzo.k9board.data.mapper.toEntity
import com.armandodarienzo.k9board.model.Word
import com.armandodarienzo.k9board.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private const val USER_WORDS_FLAG = "hand-added"

class WordRepositoryImpl(db: WordDatabase) : WordRepository {

    private val dao = db.wordDao()

    override suspend fun getWordsByCode(code: String): List<Word> =
        dao.getWordsByCode(code).map { it.toDomain() }

    override suspend fun getPlaceholderWordsByCode(code: String, queryDepth: Int): List<Word> {
        val zeros = "0".repeat(queryDepth)
        val nines = "9".repeat(queryDepth)
        return dao.getPlaceholderWordsByCode(
            prefixLength = code.length,
            lowerBound = "$code$zeros",
            upperBound = "$code$nines"
        ).map { dto ->
            Word(dto.text, dto.frequency, null, dto.originalFrequency, "false", code)
        }
    }

    override suspend fun getWordsByFlag(flag: String): List<Word> =
        dao.getWordsByFlag(flag).map { it.toDomain() }

    override fun getUserWords(): Flow<List<Word>> =
        dao.getUserWordsByFlag(USER_WORDS_FLAG).map { list -> list.map { it.toDomain() } }

    override suspend fun getMeanFrequency(): Int = dao.getMeanFrequency()

    override suspend fun getMaxLength(): Int = dao.getMaxLength()

    override suspend fun upsert(word: Word) = dao.upsert(word.toEntity())

    override suspend fun delete(wordText: String) = dao.delete(wordText)

    override suspend fun saveUserWord(wordText: String) = dao.clearFlag(wordText, USER_WORDS_FLAG)
}
