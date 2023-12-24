package io.github.colemakmods.keyboard_companion.view

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable

// Fix to allow OutlineTextField to allow click actions without affecting styling
// https://stackoverflow.com/questions/67902919/jetpack-compose-textfield-clickable-does-not-work
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun standardOutlineTextFieldColors(): TextFieldColors {
    return TextFieldDefaults.outlinedTextFieldColors(
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        disabledBorderColor = MaterialTheme.colorScheme.outline,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
        //For Icons
        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant)
}
