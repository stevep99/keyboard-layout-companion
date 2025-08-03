package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterAlt
import androidx.compose.material.icons.filled.FilterAltOff
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.options.Options
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.filter
import org.jetbrains.compose.resources.stringResource

private val log = Logger.withTag("KeyFilterToggle")

@Composable
fun KeyFilterToggle(viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (viewModel.options.keyFilterOption != Options.KeyFilter.KEY_FILTER_ALL)
        Icons.Filled.FilterAlt
    else
        Icons.Filled.FilterAltOff
    log.d("expanded = $expanded  keyFilterOption = ${viewModel.options.keyFilterOption}")
    Box(modifier = Modifier.wrapContentSize()
        .padding(horizontal = 6.dp)
        .border(1.dp, MaterialTheme.colorScheme.outline)
        .padding(horizontal = 6.dp)) {
        Row {
            Text(modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(Res.string.filter))
            IconToggleButton(
                modifier = Modifier.align(Alignment.CenterVertically),
                checked = viewModel.options.keyFilterOption == Options.KeyFilter.KEY_FILTER_ALL,
                onCheckedChange = {
                    expanded = if (!it) {
                        true
                    } else {
                        viewModel.updateKeyFilterOption(Options.KeyFilter.KEY_FILTER_ALL)
                        false
                    }
                },
            ) {
                Icon(icon, contentDescription = stringResource(Res.string.filter))
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Options.KeyFilter.entries.forEach { keyFilter ->
                DropdownMenuItem(onClick = {
                    viewModel.updateKeyFilterOption(keyFilter)
                    expanded = false
                }, text = {
                    Text(text = keyFilter.title)
                })
            }
        }
    }
}