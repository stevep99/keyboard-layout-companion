package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.colemakmods.keyboard_companion.model.Geometry
import io.github.colemakmods.keyboard_companion.options.Options
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.colors
import keyboard_layout_companion.app.generated.resources.fingers
import keyboard_layout_companion.app.generated.resources.function
import keyboard_layout_companion.app.generated.resources.split
import keyboard_layout_companion.app.generated.resources.styles
import org.jetbrains.compose.resources.stringResource

@Composable
fun ControlsPanel(viewModel: MainViewModel) {
    Row(verticalAlignment = Alignment.Bottom) {
        if (viewModel.options.modeSelectorVisible) {
            ModeDropdown(viewModel)
        }
        LayoutDropdown(viewModel)
        GeometryDropdown(viewModel)
        LayerSwitcher(viewModel)
    }
    Row {
        KeyFilterToggle(viewModel)
        ColorSchemeSelector(viewModel)
        SplitCheckBox(viewModel)
        StylesCheckBox(viewModel)
    }
}

@Composable
fun SplitCheckBox(viewModel: MainViewModel) {
    Row(modifier = Modifier
        .padding(horizontal = 6.dp)
        .border(1.dp, MaterialTheme.colorScheme.outline)
        .padding(horizontal = 6.dp)
    ) {
        Checkbox(
            checked = viewModel.options.showSplit,
            enabled = viewModel.currentGeometry?.split == Geometry.Split.SPLITTABLE,
            onCheckedChange = { viewModel.updateShowSplitOption(it) }
        )
        Text(text = stringResource(Res.string.split),
            modifier = Modifier
                .align(CenterVertically)
                .padding(end = 6.dp))
    }
}

@Composable
fun StylesCheckBox(viewModel: MainViewModel) {
    Row(modifier = Modifier
        .padding(horizontal = 6.dp)
        .border(1.dp, MaterialTheme.colorScheme.outline)
        .padding(horizontal = 6.dp)
    ) {
        Checkbox(
            checked = viewModel.options.showStyles,
            onCheckedChange = { viewModel.updateShowStylesOption(it) }
        )
        Text(text = stringResource(Res.string.styles),
            modifier = Modifier
                .align(CenterVertically)
                .padding(end = 6.dp))
    }
}

@Composable
fun ColorSchemeSelector(viewModel: MainViewModel) {
    Row(modifier = Modifier
        .padding(horizontal = 6.dp)
        .border(1.dp, MaterialTheme.colorScheme.outline)
        .padding(horizontal = 6.dp)
    ) {
        Text(text = stringResource(Res.string.colors),
            modifier = Modifier.align(CenterVertically)
        )
        RadioButton(
            selected = (viewModel.options.keyColorScheme == Options.KeyColorScheme.FINGERS),
            onClick = { viewModel.updateKeyColorSchemeOption(Options.KeyColorScheme.FINGERS) }
        )
        Text(text = stringResource(Res.string.fingers),
            modifier = Modifier.align(CenterVertically).padding(end = 6.dp)
        )
        RadioButton(
            selected = (viewModel.options.keyColorScheme == Options.KeyColorScheme.FUNCTION),
            onClick = { viewModel.updateKeyColorSchemeOption(Options.KeyColorScheme.FUNCTION) }
        )
        Text(text = stringResource(Res.string.function),
            modifier = Modifier.align(CenterVertically).padding(end = 6.dp)
        )
    }
}
