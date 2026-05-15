package com.armandodarienzo.k9board.usecase.settings

import com.armandodarienzo.k9board.repository.UserPreferencesRepository
import com.armandodarienzo.k9board.usecase.base.UseCase
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class SetLanguageUseCase(
    private val repository: UserPreferencesRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<String, Unit, Nothing>(dispatcher) {

    override suspend fun execute(parameters: String): UseCaseResult<Unit, Nothing> {
        repository.setLanguage(parameters)
        return UseCaseResult.Success(Unit)
    }
}