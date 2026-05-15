package com.armandodarienzo.k9board.data.factory

import android.content.Context
import com.armandodarienzo.k9board.data.database.WordDatabase
import com.armandodarienzo.k9board.data.repository.WordRepositoryImpl
import com.armandodarienzo.k9board.repository.WordRepository
import com.armandodarienzo.k9board.repository.WordRepositoryProvider
import com.google.android.play.core.assetpacks.AssetPackManagerFactory
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

private const val DATABASE_NAME = "dictionary"
private const val ASSETS_PATH = "databases"
private const val ASSET_PACKS_BASE_NAME = "language"
private const val LANGUAGE_TAG_AMERICAN = "en-US"

@Singleton
class WordDatabaseFactory @Inject constructor(
    @ApplicationContext private val context: Context
) : WordRepositoryProvider {
    private val databases = mutableMapOf<String, WordDatabase>()
    private val repositories = mutableMapOf<String, WordRepository>()

    @Synchronized
    override fun getForLanguage(languageTag: String): WordRepository =
        repositories.getOrPut(languageTag) {
            WordRepositoryImpl(databases.getOrPut(languageTag) { build(languageTag) })
        }

    private fun build(languageTag: String): WordDatabase {
        val dbName = "${DATABASE_NAME}_${languageTag}.sqlite"
        val assetRelPath = "$ASSETS_PATH/$dbName"
        return if (languageTag == LANGUAGE_TAG_AMERICAN) {
            WordDatabase.create(context, dbName, assetRelPath)
        } else {
            val packLocation = AssetPackManagerFactory.getInstance(context)
                .getPackLocation("${ASSET_PACKS_BASE_NAME}_${languageTag.replace("-", "_")}")
            val file = File("${packLocation?.assetsPath()}/$assetRelPath")
            WordDatabase.createFromFile(context, dbName, file)
        }
    }
}
