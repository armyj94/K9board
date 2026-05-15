package com.armandodarienzo.k9board.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.armandodarienzo.k9board.data.model.WordEntity
import com.armandodarienzo.k9board.data.model.WordPrefixDto
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {

    @Query("SELECT * FROM words WHERE t9code = :code ORDER BY frequency DESC, text ASC")
    suspend fun getWordsByCode(code: String): List<WordEntity>

    @Query("""
        SELECT substr(text, 1, :prefixLength) AS text,
               MAX(frequency) AS frequency,
               MAX(originalFrequency) AS originalFrequency
        FROM words
        WHERE t9code BETWEEN :lowerBound AND :upperBound
        GROUP BY substr(text, 1, :prefixLength)
    """)
    suspend fun getPlaceholderWordsByCode(
        prefixLength: Int,
        lowerBound: String,
        upperBound: String
    ): List<WordPrefixDto>

    @Query("SELECT * FROM words WHERE flags = :flags ORDER BY frequency DESC, text ASC")
    suspend fun getWordsByFlag(flags: String): List<WordEntity>

    @Query("SELECT * FROM words WHERE flags = :flags ORDER BY frequency DESC, text ASC")
    fun getUserWordsByFlag(flags: String): Flow<List<WordEntity>>

    @Query("SELECT AVG(frequency) FROM words")
    suspend fun getMeanFrequency(): Int

    @Query("SELECT MAX(LENGTH(text)) FROM words")
    suspend fun getMaxLength(): Int

    @Query("SELECT * FROM words WHERE text = :text LIMIT 1")
    suspend fun getByText(text: String): WordEntity?

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(entity: WordEntity)

    @Query("UPDATE words SET frequency = frequency + 1 WHERE text = :text")
    suspend fun incrementFrequency(text: String)

    @Transaction
    suspend fun upsert(entity: WordEntity) {
        if (getByText(entity.text) == null) insert(entity)
        else incrementFrequency(entity.text)
    }

    @Query("DELETE FROM words WHERE text = :wordText")
    suspend fun delete(wordText: String)

    @Query("UPDATE words SET flags = REPLACE(flags, :flag, '') WHERE text = :wordText")
    suspend fun clearFlag(wordText: String, flag: String)
}
