package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.options.Options

@Composable
fun LayoutDropdown(viewModel: MainViewModel) {
    if (viewModel.options.mode != Options.Mode.MODE_DISPLAY) return
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded)
        Icons.Filled.ArrowDropUp
    else
        Icons.Filled.ArrowDropDown
    Box(modifier = Modifier.width(300.dp).padding(horizontal = 10.dp)) {
        OutlinedTextField(
            readOnly = true,
            enabled = false,
            value = viewModel.currentLayout.toString(),
            modifier = Modifier
                .padding(end = 10.dp)
                .clickable(onClick = { expanded = !expanded }),
            colors = standardOutlineTextFieldColors(),
            label = {
                Text(stringResource(R.string.layout))
            },
            trailingIcon = {
                Icon(icon, "",
                    Modifier.clickable { expanded = !expanded })
            },
            onValueChange = {},
            maxLines = 1,
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier//.fillMaxWidth()
        ) {
            viewModel.layoutList.forEach { layout ->
                DropdownMenuItem(onClick = {
                    viewModel.updateCurrentLayout(layout)
                    expanded = false
                }, text = {
                    Text(text = layout.toString())
                })
            }
        }
    }
}