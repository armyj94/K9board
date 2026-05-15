package com.armandodarienzo.k9board.usecase.settings

import com.armandodarienzo.k9board.repository.UserPreferencesRepository
import com.armandodarienzo.k9board.usecase.base.UseCase
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class IsAutoCapsEnabledUseCase(
    private val repository: UserPreferencesRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<Unit, Boolean, Nothing>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<Boolean, Nothing> =
        UseCaseResult.Success(repository.isAutoCapsEnabled().getOrThrow())
}