package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.colemakmods.keyboard_companion.R

@Composable
fun ControlsPanel(viewModel: MainViewModel) {
    Row {
        ModeDropdown(viewModel)
        LayoutDropdown(viewModel)
        GeometryDropdown(viewModel)
    }
    Row {
        KeyFilterToggle(viewModel)
        ShowSplitCheckBox(viewModel = viewModel)
        ShowStylesCheckBox(viewModel = viewModel)
        ShowFingersCheckBox(viewModel = viewModel)
        LayerSwitcher(viewModel = viewModel)
    }
}

@Composable
fun ShowSplitCheckBox(viewModel: MainViewModel) {
    Row {
        Checkbox(
            checked = viewModel.options.showSplit,
            onCheckedChange = { viewModel.updateShowSplitOption(it) }
        )
        Text(text = stringResource(id = R.string.split),
            modifier = Modifier
                .align(CenterVertically)
                .padding(end = 10.dp))
    }
}

@Composable
fun ShowStylesCheckBox(viewModel: MainViewModel) {
    Row {
        Checkbox(
            checked = viewModel.options.showStyles,
            onCheckedChange = { viewModel.updateShowStylesOption(it) }
        )
        Text(text = stringResource(id = R.string.styles),
            modifier = Modifier
                .align(CenterVertically)
                .padding(end = 10.dp))
    }
}

@Composable
fun ShowFingersCheckBox(viewModel: MainViewModel) {
    Row {
        Checkbox(
            checked = viewModel.options.showFingers,
            onCheckedChange = { viewModel.updateShowFingersOption(it) }
        )
        Text(text = stringResource(id = R.string.fingers),
            modifier = Modifier
                .align(CenterVertically)
                .padding(end = 10.dp))
    }
}
