package com.armandodarienzo.k9board.ui.keyboard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.emoji2.emojipicker.EmojiPickerView
import com.armandodarienzo.k9board.shared.service.Key9Service
import com.armandodarienzo.k9board.R



@Composable
fun EmojiPicker(service: Key9Service?) {
    AndroidView(
        modifier = Modifier.fillMaxSize(),
        factory = { context ->
            val view = LayoutInflater.from(context).inflate(
                R.layout.emoji_picker_container, /* root = */ null, /* attachToRoot = */ false)
            val emojiPickerView = view.findViewById<EmojiPickerView>(R.id.emoji_picker).apply {
                layoutParams = ConstraintLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT,
                )
                emojiGridRows = 4.3f

            }

            emojiPickerView.setOnEmojiPickedListener{ emojiViewItem ->
                service?.emojiClick(emojiViewItem)
            }

            view
        },

    )
}
