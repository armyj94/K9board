package com.armandodarienzo.k9board.usecase.settings

import com.armandodarienzo.k9board.repository.UserPreferencesRepository
import com.armandodarienzo.k9board.usecase.base.UseCase
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class SetAutoCapsUseCase(
    private val repository: UserPreferencesRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<Boolean, Unit, Nothing>(dispatcher) {

    override suspend fun execute(parameters: Boolean): UseCaseResult<Unit, Nothing> {
        repository.setAutoCaps(parameters)
        return UseCaseResult.Success(Unit)
    }
}