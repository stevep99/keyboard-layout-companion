package io.github.colemakmods.keyboard_companion.view.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.KeyboardCompanionApplication
import io.github.colemakmods.keyboard_companion.view.MainViewModel
import io.github.colemakmods.keyboard_companion.options.keyRenderOptionRounded
import io.github.colemakmods.keyboard_companion.options.keyRenderOptionSquare
import timber.log.Timber

@Preview(name="Settings Dialog")
@Composable
fun SettingsDialogPreview() {
    SettingsDialog(viewModel = MainViewModel(KeyboardCompanionApplication()))
}

@Composable
fun SettingsDialog(viewModel: MainViewModel) {
    Timber.d("SettingsDialog ${viewModel.showSettingsDialog} ${viewModel.options.keyRenderOption}")
    val isOpenDialog = viewModel.showSettingsDialog
    if (isOpenDialog) {
        SettingsDialogUI(viewModel)
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun SettingsDialogUI(viewModel: MainViewModel) {
    AlertDialog(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(50.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        text = {
            Column (
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = stringResource(R.string.key_graphic))
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = (viewModel.options.keyRenderOption == keyRenderOptionRounded),
                        onClick = { viewModel.updateKeyRenderOption(keyRenderOptionRounded) })
                    Text(text = stringResource(id = R.string.rounded_keys))
                    RadioButton(modifier = Modifier.padding(start = 10.dp),
                        selected = (viewModel.options.keyRenderOption == keyRenderOptionSquare),
                        onClick = { viewModel.updateKeyRenderOption(keyRenderOptionSquare) })
                    Text(text = stringResource(id = R.string.square_keys))
                }
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.showSettingsDialog = false }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        onDismissRequest = { viewModel.showSettingsDialog = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    )
}
