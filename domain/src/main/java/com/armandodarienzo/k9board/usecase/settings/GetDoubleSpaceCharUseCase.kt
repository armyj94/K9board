package com.armandodarienzo.k9board.usecase.settings

import com.armandodarienzo.k9board.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.repository.UserPreferencesRepository
import com.armandodarienzo.k9board.usecase.base.UseCase
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class GetDoubleSpaceCharUseCase(
    private val repository: UserPreferencesRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<Unit, DoubleSpaceCharacter, Nothing>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<DoubleSpaceCharacter, Nothing> =
        UseCaseResult.Success(repository.getDoubleSpaceCharacter().getOrThrow())
}