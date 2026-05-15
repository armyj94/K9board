package com.armandodarienzo.k9board.data.di

import com.armandodarienzo.k9board.data.factory.WordDatabaseFactory
import com.armandodarienzo.k9board.repository.WordRepositoryProvider
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DatabaseModule {

    @Binds
    @Singleton
    abstract fun bindWordRepositoryProvider(
        factory: WordDatabaseFactory
    ): WordRepositoryProvider
}
