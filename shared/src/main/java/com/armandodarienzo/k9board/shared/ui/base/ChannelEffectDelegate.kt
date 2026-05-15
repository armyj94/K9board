package com.armandodarienzo.k9board.shared.ui.base

import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.receiveAsFlow

class ChannelEffectDelegate<Effect : Reducer.SideEffect> : EffectDelegate<Effect> {
    private val channel = Channel<Effect>(Channel.BUFFERED)
    override val effect: Flow<Effect> = channel.receiveAsFlow()
    override fun sendEffect(effect: Effect) { channel.trySend(effect) }
    fun close() { channel.close() }
}