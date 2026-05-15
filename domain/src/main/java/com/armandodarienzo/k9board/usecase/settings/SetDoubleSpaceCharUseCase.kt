package com.armandodarienzo.k9board.usecase.settings

import com.armandodarienzo.k9board.model.DoubleSpaceCharacter
import com.armandodarienzo.k9board.repository.UserPreferencesRepository
import com.armandodarienzo.k9board.usecase.base.UseCase
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class SetDoubleSpaceCharUseCase(
    private val repository: UserPreferencesRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<DoubleSpaceCharacter, Unit, Nothing>(dispatcher) {

    override suspend fun execute(parameters: DoubleSpaceCharacter): UseCaseResult<Unit, Nothing> {
        repository.setDoubleSpaceCharacter(parameters)
        return UseCaseResult.Success(Unit)
    }
}