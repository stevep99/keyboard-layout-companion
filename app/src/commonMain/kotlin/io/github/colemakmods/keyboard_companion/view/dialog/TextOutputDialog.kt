package io.github.colemakmods.keyboard_companion.view.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.TextButton
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.platform.Common
import io.github.colemakmods.keyboard_companion.view.MainViewModel
import io.github.colemakmods.keyboard_companion.view.MainViewModel.KeyEventAction.*
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.ok
import keyboard_layout_companion.app.generated.resources.copy_to_clipboard
import org.jetbrains.compose.resources.stringResource

private val log = Logger.withTag("TextOutputDialog")

@Composable
fun TextOutputDialog(viewModel: MainViewModel) {
    log.d("TextOutputDialog ${viewModel.showTextOutputDialog}")
    if (viewModel.showTextOutputDialog) {
        TextOutputDialogUI(viewModel)
    }
}

@Composable
private fun TextOutputDialogUI(viewModel: MainViewModel) {
    AlertDialog(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(30.dp)
            .wrapContentHeight(),
        text = {
            val clipboard = LocalClipboardManager.current
            val scrollState = rememberScrollState()
            OutlinedCard(
                modifier = Modifier.wrapContentHeight()
            ) {
                Column {
                    Column(modifier = Modifier.verticalScroll(scrollState)
                        .weight(1f)) {
                        SelectionContainer {
                            Text(
                                text = viewModel.textOutputContent ?: "",
                                fontFamily = FontFamily.Monospace,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(6.dp).fillMaxWidth()
                            )
                        }
                    }
                    Divider()
                    TextButton(onClick = {
                        clipboard.setText(AnnotatedString(viewModel.textOutputContent ?: ""))
                    }) {
                        Text(stringResource(Res.string.copy_to_clipboard))
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.onEvent(TextOutputAction(false)) }) {
                Text(text = stringResource(Res.string.ok))
            }
        },
        onDismissRequest = { viewModel.onEvent(TextOutputAction(false)) },
        properties = DialogProperties(usePlatformDefaultWidth = Common.platform.name != "android"),
    )
}
