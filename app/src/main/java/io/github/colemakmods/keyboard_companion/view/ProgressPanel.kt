package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.width
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ProgressPanel() {
    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        CircularProgressIndicator(
            modifier = Modifier.width(64.dp)
                .align(Alignment.Center)
        )
    }
}
