package com.armandodarienzo.k9board.viewmodel

import android.content.res.Configuration
import android.os.Build
import android.view.View
import androidx.annotation.RequiresApi
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewTreeLifecycleOwner
import androidx.lifecycle.ViewTreeViewModelStoreOwner
import androidx.savedstate.setViewTreeSavedStateRegistryOwner
import com.armandodarienzo.k9board.shared.Key9Service
import com.armandodarienzo.k9board.ui.keyboard.ComposeKeyboardView

class Key9ServiceMobile: Key9Service() {

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onCreateInputView(): View {

        //background color needs to be changed here, otherwise system won't pick up correctly
        var backgroundColorId = android.R.color.system_accent1_50

        val nightModeFlags: Int = this.resources.configuration.uiMode and
                Configuration.UI_MODE_NIGHT_MASK

        backgroundColorId = when (nightModeFlags) {
            Configuration.UI_MODE_NIGHT_YES -> android.R.color.system_neutral1_900
            else -> android.R.color.system_neutral2_50
        }

        view = ComposeKeyboardView(this, backgroundColorId)

        window!!.window!!.decorView.let { decorView ->
            ViewTreeLifecycleOwner.set(decorView, this)
            ViewTreeViewModelStoreOwner.set(decorView, this)
//            ViewTreeSavedStateRegistryOwner.set(decorView, this)
            decorView.setViewTreeSavedStateRegistryOwner(this)
        }
        window!!.window!!.navigationBarColor = this.getColor(backgroundColorId)
        view.let {
            ViewTreeLifecycleOwner.set(it, this)
            ViewTreeViewModelStoreOwner.set(it, this)
//            ViewTreeSavedStateRegistryOwner.set(it, this)
            it.setViewTreeSavedStateRegistryOwner(this)
        }

        return super.onCreateInputView()
    }

}