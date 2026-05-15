package com.armandodarienzo.k9board.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "words",
    indices = [
        Index(name = "orderIndex", value = ["frequency", "text"], orders = [Index.Order.DESC, Index.Order.ASC]),
        Index(name = "codeIndex", value = ["t9code"], orders = [Index.Order.ASC])
    ]
)
data class WordEntity(
    @PrimaryKey
    @ColumnInfo(name = "text")              val text: String,
    @ColumnInfo(name = "frequency")         val frequency: Int,
    @ColumnInfo(name = "flags")             val flags: String?,
    @ColumnInfo(name = "originalFrequency") val originalFrequency: Int,
    @ColumnInfo(name = "possiblyOffensive") val possiblyOffensive: String?,
    @ColumnInfo(name = "t9code")            val t9code: String
)
