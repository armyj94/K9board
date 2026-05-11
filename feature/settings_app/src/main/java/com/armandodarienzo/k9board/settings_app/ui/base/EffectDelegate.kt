package com.armandodarienzo.k9board.settings_app.ui.base

import kotlinx.coroutines.flow.Flow

interface EffectDelegate<Effect : Reducer.SideEffect> {
    val effect: Flow<Effect>
    fun sendEffect(effect: Effect)
}