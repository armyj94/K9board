package com.armandodarienzo.k9board.usecase.settings

import com.armandodarienzo.k9board.model.KeyboardSize
import com.armandodarienzo.k9board.repository.UserPreferencesRepository
import com.armandodarienzo.k9board.usecase.base.UseCase
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class SetKeyboardSizeUseCase(
    private val repository: UserPreferencesRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<KeyboardSize, Unit, Nothing>(dispatcher) {

    override suspend fun execute(parameters: KeyboardSize): UseCaseResult<Unit, Nothing> {
        repository.setKeyboardSize(parameters)
        return UseCaseResult.Success(Unit)
    }
}