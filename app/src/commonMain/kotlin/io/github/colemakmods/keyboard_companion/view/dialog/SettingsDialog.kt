package io.github.colemakmods.keyboard_companion.view.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.options.KeyRenderOptionRounded
import io.github.colemakmods.keyboard_companion.options.KeyRenderOptionSquare
import io.github.colemakmods.keyboard_companion.platform.Common
import io.github.colemakmods.keyboard_companion.view.MainViewModel
import io.github.colemakmods.keyboard_companion.view.MainViewModel.KeyEventAction.*
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.key_style
import keyboard_layout_companion.app.generated.resources.ok
import keyboard_layout_companion.app.generated.resources.rounded_keys
import keyboard_layout_companion.app.generated.resources.square_keys
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val log = Logger.withTag("SettingsDialog")

@Preview
@Composable
fun SettingsDialogPreview() {
    SettingsDialog(viewModel = MainViewModel())
}

@Composable
fun SettingsDialog(viewModel: MainViewModel) {
    log.d("SettingsDialog ${viewModel.showSettingsDialog} ${viewModel.options.keyRenderOption}")
    if (viewModel.showSettingsDialog) {
        SettingsDialogUI(viewModel)
    }
}

@Composable
private fun SettingsDialogUI(viewModel: MainViewModel) {
    AlertDialog(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(50.dp)
            .wrapContentHeight(),
        text = {
            Column {
                Text(text = stringResource(Res.string.key_style))
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = (viewModel.options.keyRenderOption == KeyRenderOptionRounded),
                        onClick = { viewModel.updateKeyRenderOption(KeyRenderOptionRounded) })
                    Text(text = stringResource(Res.string.rounded_keys))
                    RadioButton(modifier = Modifier.padding(start = 10.dp),
                        selected = (viewModel.options.keyRenderOption == KeyRenderOptionSquare),
                        onClick = { viewModel.updateKeyRenderOption(KeyRenderOptionSquare) })
                    Text(text = stringResource(Res.string.square_keys))
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = viewModel.options.modeSelectorVisible,
                        onCheckedChange = { viewModel.updateModeSelectorVisible(it) }
                    )
                    Text(
                        text = "Show Mode selector",
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.onEvent(SettingsDialogAction(false)) }) {
                Text(text = stringResource(Res.string.ok))
            }
        },
        onDismissRequest = { viewModel.onEvent(InfoDialogAction(false)) },
        properties = DialogProperties(usePlatformDefaultWidth = Common.platform.name != "android"),
    )
}
