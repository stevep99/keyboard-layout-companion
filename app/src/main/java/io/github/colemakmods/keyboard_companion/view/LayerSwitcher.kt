package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.colemakmods.keyboard_companion.R

@Composable
fun LayerSwitcher(viewModel: MainViewModel) {
    if (viewModel.layerCount() > 1) {
        Row (modifier = Modifier.padding(horizontal = 20.dp)) {
            Text(stringResource(id = R.string.layers),
                modifier = Modifier.align(Alignment.CenterVertically))
            IconButton(onClick = { viewModel.previousLayer() }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_left),
                    modifier = Modifier.size(30.dp),
                    contentDescription = null
                )
            }
            IconButton(onClick = { viewModel.nextLayer() }) {
                Icon(
                    painter = painterResource(id = R.drawable.arrow_right),
                    modifier = Modifier.size(30.dp),
                    contentDescription = null
                )
            }
            Text(text = viewModel.currentLayerName(),
                modifier = Modifier.align(Alignment.CenterVertically))
        }
    }
}