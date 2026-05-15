package com.armandodarienzo.k9board.usecase.base

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

abstract class UseCase<in Parameters, Success, BusinessRuleError>(
    private val dispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(parameters: Parameters): UseCaseResult<Success, BusinessRuleError> {
        return try {
            withContext(dispatcher) {
                execute(parameters)
            }
        } catch (e: Throwable) {
            UseCaseResult.Error(e.mapToAppError())
        }
    }

    protected abstract suspend fun execute(parameters: Parameters): UseCaseResult<Success, BusinessRuleError>
}