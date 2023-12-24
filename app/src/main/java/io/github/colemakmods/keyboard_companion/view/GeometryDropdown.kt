package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import io.github.colemakmods.keyboard_companion.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeometryDropdown(viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded)
        Icons.Filled.ArrowDropUp
    else
        Icons.Filled.ArrowDropDown
    Box(modifier = Modifier.width(250.dp).padding(horizontal = 10.dp)) {
        OutlinedTextField(
            readOnly = true,
            enabled = false,
            value = viewModel.currentGeometry.toString(),
            modifier = Modifier.padding(end = 10.dp)
                .clickable(onClick = { expanded = !expanded }),
            colors = standardOutlineTextFieldColors(),
            label = {
                Text(text = stringResource(R.string.geometry))
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
            viewModel.geometryList.forEach { geometry ->
                DropdownMenuItem(onClick = {
                    viewModel.currentGeometry = geometry
                    expanded = false
                }, text = {
                    Text(text = geometry.toString())
                })
            }
        }
    }
}
