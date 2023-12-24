package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.model.Geometry
import io.github.colemakmods.keyboard_companion.options.Options

@Composable
fun KeyboardPanel(viewModel: MainViewModel, printMode: Boolean = false) {
    val layout = viewModel.currentLayout ?: return
    val geometry = viewModel.currentGeometry ?: return
    val showMultiLayers = viewModel.currentLayout?.isLayerMulti(viewModel.currentLayer) == true
            && viewModel.options.mode == Options.Mode.MODE_DISPLAY
    val mainLayer = if (showMultiLayers) viewModel.currentLayer + 1 else viewModel.currentLayer

    val boxHeight = dimensionResource(if (printMode) R.dimen.key_size_print else R.dimen.key_size_disp)
        .times(Geometry.ROW.values().size)
    var y = 0.0f
    Box(modifier = Modifier.padding(horizontal = 10.dp).height(boxHeight)) {
        for (row in Geometry.ROW.values()) {
            for (i in 0 until geometry.getRowLength(row)) {
                val key = geometry.getKey(row, i)
                if (key != null) {

                    //special case - deal with split spacebar
                    var visible = true
                    if ("SPC" == key.keyId) {
                        if (viewModel.options.showSplit) visible = false
                    } else if ("LSPC" == key.keyId || "RSPC" == key.keyId) {
                        if (!viewModel.options.showSplit) visible = false
                    }

                    val keyHighlight = viewModel.options.showStyles && key.highlight
                    val keyGraphicResource = viewModel.getKeyGraphicResource(layout.getGraphic(mainLayer, key.keyId))
                    val color = layout.getColor(mainLayer, key.keyId)

                    val keyMainLabel = when (viewModel.options.mode) {
                        Options.Mode.MODE_DISPLAY -> viewModel.formatLabel(layout.getLabel(mainLayer, key.keyId))
                        Options.Mode.MODE_SCORE -> viewModel.formatValue(key.score)
                        Options.Mode.MODE_DISTANCE -> viewModel.formatValue(key.distance)
                    }
                    //Timber.d("row $row col $i key_disp ${key.keyId} ${key.finger}  = $keyMainLabel [$color]")

                    val alpha = viewModel.selectKeyAlpha(color)

                    val x = if (viewModel.options.showSplit && key.finger >= 5)
                            key.x + 1.0f else key.x

                    val keyDrawable = viewModel.selectKeyDrawable(key, row, keyMainLabel, color, viewModel.options)
                    val backgroundColor = viewModel.options.keyRenderOption.background(keyHighlight)
                    val textDecoration = viewModel.options.keyRenderOption.textHighlightStyle(keyHighlight)
                    val eventListener: (MainViewModel.KeyEventAction) -> Unit = {
                        when (it) {
                            is MainViewModel.KeyEventAction.KeyDragAction -> {
                                viewModel.handleDragDrop(key, it.dx, it.dy)
                            }
                            is MainViewModel.KeyEventAction.KeyClickAction -> {
                                viewModel.handleClick(key)
                            }
                        }
                    }

                    if (keyDrawable != 0 && visible) {
                        val keyCornerLabels = if (showMultiLayers) viewModel.getMultiLabels(layout, mainLayer, key) else null
                        KeyPanel(x = x, y = y, key.width,
                            keyImage = keyDrawable,
                            backgroundColor = backgroundColor,
                            foregroundImage = keyGraphicResource,
                            alpha = alpha,
                            keyMainLabel,
                            textDecoration,
                            keyCornerLabels,
                            printMode,
                            eventListener,
                        )
                    }
                }
            }
            y += 1.0f
        }

    }

}
