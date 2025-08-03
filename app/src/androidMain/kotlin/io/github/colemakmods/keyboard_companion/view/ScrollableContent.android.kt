package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
actual fun ScrollableContent(
    scrollState: ScrollState,
    content: @Composable (Modifier) -> Unit
) {
    content(Modifier)
}
