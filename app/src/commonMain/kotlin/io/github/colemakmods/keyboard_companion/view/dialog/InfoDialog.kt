package io.github.colemakmods.keyboard_companion.view.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import co.touchlab.kermit.Logger
import com.mikepenz.markdown.m3.Markdown
import io.github.colemakmods.keyboard_companion.platform.Common
import io.github.colemakmods.keyboard_companion.view.MainViewModel
import io.github.colemakmods.keyboard_companion.view.MainViewModel.KeyEventAction.InfoDialogAction
import io.github.colemakmods.keyboard_companion.view.ScrollableContent
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.app_title
import keyboard_layout_companion.app.generated.resources.ok
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview

private val log = Logger.withTag("InfoDialog")

@Preview
@Composable
fun InfoDialogPreview() {
    InfoDialog(viewModel = MainViewModel())
}

@Composable
fun InfoDialog(viewModel: MainViewModel) {
    log.d("InfoDialog ${viewModel.showInfoDialog}")
    if (viewModel.showInfoDialog) {
        InfoDialogUI(viewModel)
    }
}

@Composable
private fun InfoDialogUI(viewModel: MainViewModel) {
    val scrollState = rememberScrollState()
    AlertDialog(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxHeight(0.9f)
            .wrapContentHeight(),
        text = {
            ScrollableContent(scrollState) { modifier ->
                Column(modifier.verticalScroll(scrollState)) {
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            modifier = Modifier.weight(1f).padding(vertical = 6.dp),
                            style = MaterialTheme.typography.headlineSmall,
                            text = stringResource(Res.string.app_title)
                        )
                        Text(
                            modifier = Modifier.padding(vertical = 6.dp),
                            text = "v${Common.platform.getVersionText()}"
                        )
                    }
                    viewModel.infoDialogContent?.let { content ->
                        Markdown(content)
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = { viewModel.onEvent(InfoDialogAction(false)) }) {
                Text(text = stringResource(Res.string.ok))
            }
        },
        onDismissRequest = { viewModel.onEvent(InfoDialogAction(false)) },
        properties = DialogProperties(usePlatformDefaultWidth = Common.platform.name != "android"),
    )
}


