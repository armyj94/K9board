package com.armandodarienzo.k9board.usecase.settings

import com.armandodarienzo.k9board.model.KeyboardSize
import com.armandodarienzo.k9board.repository.UserPreferencesRepository
import com.armandodarienzo.k9board.usecase.base.UseCase
import com.armandodarienzo.k9board.usecase.base.UseCaseResult
import kotlinx.coroutines.CoroutineDispatcher

class GetKeyboardSizeUseCase(
    private val repository: UserPreferencesRepository,
    dispatcher: CoroutineDispatcher
) : UseCase<Unit, KeyboardSize, Nothing>(dispatcher) {

    override suspend fun execute(parameters: Unit): UseCaseResult<KeyboardSize, Nothing> =
        UseCaseResult.Success(repository.getKeyboardSize().getOrThrow())
}