package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.github.colemakmods.keyboard_companion.LocalMainViewModel
import io.github.colemakmods.keyboard_companion.view.MainViewModel.KeyEventAction.*
import io.github.colemakmods.keyboard_companion.view.dialog.InfoDialog
import io.github.colemakmods.keyboard_companion.view.dialog.KeyEditDialog
import io.github.colemakmods.keyboard_companion.view.dialog.SettingsDialog
import io.github.colemakmods.keyboard_companion.view.dialog.TextOutputDialog

@Composable
fun App() {
    val viewModel = LocalMainViewModel.current

    MaterialTheme(
        shapes = Shapes(
            small = RoundedCornerShape(4.dp),
            medium = RoundedCornerShape(4.dp),
            large = RoundedCornerShape(0.dp)
        )
    ) {
        Scaffold(
            topBar = { AppTopBar(viewModel) }
        ) { innerPadding ->
            InfoDialog(viewModel)
            SettingsDialog(viewModel)
            TextOutputDialog(viewModel)
            viewModel.currentLayout?.let {
                KeyEditDialog(viewModel,
                    layout = it,
                    onCancelled = { viewModel.onEvent(SelectKeyAction(null)) }
                ) { labels, finger, highlight ->
                    viewModel.onEvent(KeyEditConfirmAction(labels, finger, highlight))
                }
            }
            Column(modifier = Modifier.padding(innerPadding)){
                ControlsPanel(viewModel)
                key(viewModel.drawRefreshState) {
                    Box {
                        KeyboardPanel(viewModel)
                        if (viewModel.printMode) {
                            ProgressPanel()
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(viewModel: MainViewModel) =
    TopAppBar(
        title = { Text("Keyboard Layout Companion") },
        actions = {
            // Action for "Info"
            IconButton(onClick = { viewModel.onEvent(InfoDialogAction(true)) }) {
                Icon(
                    imageVector = Icons.Filled.Info,
                    contentDescription = "Information"
                )
            }

            IconButton(onClick = { viewModel.onEvent(SettingsDialogAction(true)) }) {
                Icon(
                    imageVector = Icons.Filled.Settings,
                    contentDescription = "Settings"
                )
            }

            IconButton(onClick = { viewModel.onEvent(SaveImageAction) }) {
                Icon(
                    imageVector = Icons.Filled.Save,
                    contentDescription = "Save"
                )
            }

            IconButton(onClick = { viewModel.onEvent(TextOutputAction(true)) }) {
                Icon(
                    imageVector = Icons.Filled.ContentCopy,
                    contentDescription = "Output Text"
                )
            }

        }
    )

object AppColor {
    val keyTextMainColor = Color(0xFF222222)
    val keyTextMinorColor = Color(0xFF333333)
    val keyLcColor = Color(0xff80c4c4)  //light cyan
    val keyLpColor = Color(0xffca80ca)  //light purple
    val keyLgColor = Color(0xff80b780)  //light green
    val keyLbColor = Color(0xff7a93c0)  //light blue
    val keyLyColor = Color(0xffd3d37b)  //light yellow
    val keyLrColor = Color(0xffd98080)  //light red
    val keyDcColor = Color(0xff60a4a4)  //darker cyan
    val keyDpColor = Color(0xffae66ae)  //darker purple
    val keyDgColor = Color(0xff669c67)  //darker green
    val keyDbColor = Color(0xff8080ca)  //darker blue
    val keyDyColor = Color(0xffb1b262)  //darker yellow
    val keyDrColor = Color(0xffcf5e5e)  //darker red
    val keyXColor = Color.LightGray
}

object AppDimens {
    val keySize = 32.dp
    val keyTextSizeSingle = 16.sp
    val keyTextSizeMany = 14.sp
    val keyTextShadowSize = 1f
    val keyTextSmallSize = 8.sp
    val keyTextSmallMarginHoriz = 2.dp
    val keyTextSmallMarginVert = 1.dp
}