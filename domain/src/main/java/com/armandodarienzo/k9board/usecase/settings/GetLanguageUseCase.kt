package com.armandodarienzo.k9board.usecase.settings

import com.armandodarienzo.k9board.repository.UserPreferencesRepository
import com.armandodarienzo.k9board.usecase.base.UseCase
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class GetLanguageUseCase(
    private val repository: UserPreferencesRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<Unit, String, Nothing>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<String, Nothing> =
        UseCaseResult.Success(repository.getLanguage().getOrThrow())
}