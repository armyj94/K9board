package com.armandodarienzo.k9board.data.mapper

import com.armandodarienzo.k9board.data.model.WordEntity
import com.armandodarienzo.k9board.model.Word

fun WordEntity.toDomain(): Word =
    Word(text, frequency, flags, originalFrequency, possiblyOffensive ?: "false", t9code)

fun Word.toEntity(): WordEntity =
    WordEntity(
        text = text,
        frequency = frequency ?: 0,
        flags = flags,
        originalFrequency = originalFrequency ?: 0,
        possiblyOffensive = possiblyOffensive,
        t9code = t9Code ?: ""
    )
