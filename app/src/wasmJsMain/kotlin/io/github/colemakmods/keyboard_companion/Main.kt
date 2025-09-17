package io.github.colemakmods.keyboard_companion

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeViewport
import io.github.colemakmods.keyboard_companion.platform.Common
import io.github.colemakmods.keyboard_companion.platform.WasmPlatform
import io.github.colemakmods.keyboard_companion.view.App

private const val VERSION = "1.11"

fun main() {

    Common.platform = WasmPlatform(VERSION)

    @OptIn(ExperimentalComposeUiApi::class)
    ComposeViewport {
        Box(Modifier.fillMaxSize()) {
            App()
        }
    }
}
