package com.armandodarienzo.k9board.usecase.base

sealed class UseCaseResult<out D, out E> {
    data class Success<out D>(val successData: D) : UseCaseResult<D, Nothing>()
    data class Error(val error: AppError) : UseCaseResult<Nothing, Nothing>()
    data class BusinessRuleError<out E>(val error: E) : UseCaseResult<Nothing, E>()

    fun isSuccessful() = this is Success
    fun hasFailed() = this is Error || this is BusinessRuleError<*>
}