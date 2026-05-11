package com.armandodarienzo.k9board.usecase.base

data class AppError(val originalException: Throwable)

fun Throwable.mapToAppError(): AppError = AppError(this)