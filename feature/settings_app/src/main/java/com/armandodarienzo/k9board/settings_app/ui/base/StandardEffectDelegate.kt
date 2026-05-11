package com.armandodarienzo.k9board.settings_app.ui.base

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow

class StandardEffectDelegate<Effect : Reducer.SideEffect> : EffectDelegate<Effect> {

    private val _effects = MutableSharedFlow<Effect>(
        replay = 0,
        extraBufferCapacity = 64,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val effect = _effects.asSharedFlow()

    override fun sendEffect(effect: Effect) {
        _effects.tryEmit(effect)
    }
}