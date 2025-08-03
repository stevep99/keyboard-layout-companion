package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import io.github.colemakmods.keyboard_companion.options.Options
import io.github.colemakmods.keyboard_companion.view.MainViewModel.KeyEventAction.SelectModeAction
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.mode
import org.jetbrains.compose.resources.stringResource

@Composable
fun ModeDropdown(viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(6.dp)) {
        Text(
            modifier = Modifier.padding(horizontal = 4.dp),
            style = MaterialTheme.typography.bodyMedium,
            text = stringResource(Res.string.mode)
        )
        OutlinedButton(
            onClick = { expanded = !expanded },
        ) {
            Text(
                modifier = Modifier.defaultMinSize(minWidth = 80.dp)
                    .padding(horizontal = 4.dp),
                textAlign = TextAlign.Left,
                text = viewModel.options.mode.title
            )
            val dropdownIcon = if (expanded)
                Icons.Filled.ArrowDropUp
            else
                Icons.Filled.ArrowDropDown
            Icon(dropdownIcon, contentDescription = null)
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Options.Mode.entries.forEach { mode ->
                DropdownMenuItem(onClick = {
                    viewModel.onEvent(SelectModeAction(mode))
                    expanded = false
                }, text = {
                    Text(text = mode.title)
                })
            }
        }
    }
}
