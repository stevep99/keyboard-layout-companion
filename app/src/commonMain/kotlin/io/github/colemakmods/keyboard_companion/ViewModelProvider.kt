package io.github.colemakmods.keyboard_companion

import androidx.compose.runtime.staticCompositionLocalOf
import io.github.colemakmods.keyboard_companion.view.MainViewModel

val LocalMainViewModel = staticCompositionLocalOf {
    MainViewModel()
    //error("No MainViewModel provided")
}
