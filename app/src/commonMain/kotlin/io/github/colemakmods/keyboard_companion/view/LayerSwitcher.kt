package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.arrow_left
import keyboard_layout_companion.app.generated.resources.arrow_right
import keyboard_layout_companion.app.generated.resources.layers
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LayerSwitcher(viewModel: MainViewModel) {
    if (viewModel.layerCount() > 1) {
        Row(modifier = Modifier
            .padding(6.dp)
            .border(1.dp, MaterialTheme.colorScheme.outline)
            .padding(horizontal = 6.dp)
        ) {
            Text(stringResource(Res.string.layers),
                modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(onClick = { viewModel.previousLayer() }) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_left),
                    modifier = Modifier.size(30.dp),
                    contentDescription = null
                )
            }
            IconButton(onClick = { viewModel.nextLayer() }) {
                Icon(
                    painter = painterResource(Res.drawable.arrow_right),
                    modifier = Modifier.size(30.dp),
                    contentDescription = null
                )
            }
            Text(text = viewModel.currentLayerName(),
                modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}