package io.github.colemakmods.keyboard_companion.view.dialog

import android.webkit.WebView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.DialogProperties
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.KeyboardCompanionApplication
import io.github.colemakmods.keyboard_companion.view.MainViewModel
import timber.log.Timber

@Preview(name="Info Dialog")
@Composable
fun InfoDialogPreview() {
    InfoDialog(viewModel = MainViewModel(KeyboardCompanionApplication()))
}

@Composable
fun InfoDialog(viewModel: MainViewModel) {
    Timber.d("InfoDialog ${viewModel.showInfoDialog}")
    val isOpenDialog = viewModel.showInfoDialog
    if (isOpenDialog) {
        InfoDialogUI(viewModel)
    }
}

@Composable
private fun InfoDialogUI(viewModel: MainViewModel) {
    AlertDialog(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(50.dp)
            .fillMaxWidth()
            .wrapContentHeight(),
        text = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        modifier = Modifier.weight(1f, true),
                        style = Typography().headlineMedium,
                        text = stringResource(id = R.string.app_title),
                    )
                    Text(text = viewModel.appVersionText)
                }
                AndroidView(
                    factory = {
                        val webView = WebView(it)
                        webView.settings.textZoom = 80
                        webView.loadUrl("file:///android_asset/info.html")
                        webView
                    })
            }

        },
        confirmButton = {
            Button(onClick = { viewModel.showInfoDialog = false }) {
                Text(text = stringResource(id = android.R.string.ok))
            }
        },
        onDismissRequest = { viewModel.showInfoDialog = false },
        properties = DialogProperties(usePlatformDefaultWidth = false),
    )
}
