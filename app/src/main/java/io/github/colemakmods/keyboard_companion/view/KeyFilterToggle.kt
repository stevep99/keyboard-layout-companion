package io.github.colemakmods.keyboard_companion.view

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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.options.Options
import timber.log.Timber

@Composable
fun KeyFilterToggle(viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (viewModel.options.keyFilterOption != Options.KeyFilter.KEY_FILTER_ALL)
        Icons.Filled.FilterAlt
    else
        Icons.Filled.FilterAltOff
    Timber.d("expanded = $expanded  keyFilterOption = ${viewModel.options.keyFilterOption}")
    Box(modifier = Modifier.wrapContentSize().padding(horizontal = 10.dp)) {
        Row {
            Text(modifier = Modifier.align(Alignment.CenterVertically),
                text = stringResource(id = R.string.filter))
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
                Icon(icon, contentDescription = stringResource(R.string.filter))
            }
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
        ) {
            Options.KeyFilter.values().forEach { keyFilter ->
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