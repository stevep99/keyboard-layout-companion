package io.github.colemakmods.keyboard_companion.view.dialog

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.model.Key
import io.github.colemakmods.keyboard_companion.model.Layout
import io.github.colemakmods.keyboard_companion.model.formattedLabel
import io.github.colemakmods.keyboard_companion.view.MainViewModel
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.ok
import org.jetbrains.compose.resources.stringResource

private val log = Logger.withTag("KeyEditDialog")

@Composable
fun KeyEditDialog(viewModel: MainViewModel, layout: Layout,
                  onCancelled: () -> Unit,
                  onChanged: (List<String?>, Int?, Boolean) -> Unit) {
    val currentKey = viewModel.currentKey
    if (currentKey != null) {
        log.d("KeyEditDialog $currentKey")
        KeyEditDialogUI(currentKey, layout, onChanged, onCancelled)
    }
}

@Composable
private fun KeyEditDialogUI(key: Key, layout: Layout,
                            onChanged: (List<String?>, Int?, Boolean) -> Unit,
                            onCancelled: () -> Unit) {
    val labelStates = remember {
        List(layout.mapping.getLayerCount()) {
            mutableStateOf(layout.mapping.getLayer(it).getLabel(key.id))
        }
    }
    var finger by remember { mutableStateOf(key.finger.toString()) }
    var highlight by remember { mutableStateOf(key.highlight) }
    val scrollState = rememberScrollState()
    AlertDialog(
        title = { Text("Key Editor") },
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .padding(50.dp)
            .fillMaxWidth(0.5f)
            .wrapContentHeight(),
        text = {
            Column(modifier = Modifier.verticalScroll(scrollState)) {
                FlowRow(maxItemsInEachRow = 2) {
                    labelStates.forEachIndexed { index, label ->
                        if (labelStates.size > 1 && index == 0) return@forEachIndexed //skip first for multi-layer
                        val layerName = layout.mapping.getLayer(index).name
                        OutlinedTextField(
                            value = label.value?.formattedLabel() ?: "",
                            onValueChange = { label.value = it.take(4) },
                            modifier = Modifier.padding(6.dp).width(200.dp),
                            label = { Text("Label - $layerName") },
                            singleLine = true,
                        )
                    }
                }
                OutlinedTextField(
                    value = finger,
                    onValueChange = { finger = it.takeWhile { it.isDigit() }.take(1) },
                    modifier = Modifier.padding(6.dp).width(200.dp),
                    label = { Text("Finger") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                )
                Row(modifier = Modifier.padding(6.dp),
                    verticalAlignment = Alignment.CenterVertically) {
                   Checkbox(
                       checked = highlight,
                       onCheckedChange = { highlight = it },
                   )
                   Text(
                       text = "Is Home Key",
                   )
                }
            }

        },
        confirmButton = {
            Button(onClick = {
                val labels = labelStates.map { it.value }
                onChanged(labels, finger.toIntOrNull(), highlight)
            }) {
                Text(text = stringResource(Res.string.ok))
            }
        },
        onDismissRequest = onCancelled,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    )

}
