package io.github.colemakmods.keyboard_companion

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.window.CanvasBasedWindow
import io.github.colemakmods.keyboard_companion.platform.Common
import io.github.colemakmods.keyboard_companion.platform.WasmPlatform
import io.github.colemakmods.keyboard_companion.view.App

private const val VERSION = "1.10"

fun main() {

    Common.platform = WasmPlatform(VERSION)

    @OptIn(ExperimentalComposeUiApi::class)
    CanvasBasedWindow(title = "Keyboard Layout Companion") {
        App()
    }
}
