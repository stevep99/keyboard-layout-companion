package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
actual fun ScrollableContent(scrollState: ScrollState, content: @Composable (Modifier) -> Unit) {
    Row(Modifier.fillMaxWidth()) {
        content(Modifier.weight(1f).padding(10.dp))
        VerticalScrollbar(
            modifier = Modifier.fillMaxHeight()
                .width(10.dp),
            adapter = rememberScrollbarAdapter(scrollState)
        )
    }
}
