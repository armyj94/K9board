package com.armandodarienzo.k9board.keyboard.usecase

import com.armandodarienzo.k9board.model.KeyboardCapsStatus
import javax.inject.Inject

class ToggleCapsStateUseCase @Inject constructor() {
    operator fun invoke(
        current: KeyboardCapsStatus,
        isManual: Boolean,
        lastShiftMs: Long,
        nowMs: Long,
    ): KeyboardCapsStatus = when {
        (nowMs - lastShiftMs < 500L && current == KeyboardCapsStatus.UPPER_CASE) ||
        (isManual && current == KeyboardCapsStatus.LOWER_CASE) -> KeyboardCapsStatus.CAPS_LOCK
        current == KeyboardCapsStatus.LOWER_CASE -> KeyboardCapsStatus.UPPER_CASE
        else -> KeyboardCapsStatus.LOWER_CASE
    }
}
