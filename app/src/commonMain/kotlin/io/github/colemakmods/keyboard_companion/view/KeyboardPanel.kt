package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.layer.drawLayer
import androidx.compose.ui.graphics.rememberGraphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.model.formatToOneDecimalPlace
import io.github.colemakmods.keyboard_companion.model.formattedLabel
import io.github.colemakmods.keyboard_companion.options.Options
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.seconds

private val log = Logger.withTag("KeyboardPanel")

@Composable
fun KeyboardPanel(viewModel: MainViewModel) {
    val printMode = viewModel.printMode
    log.d("KeyboardPanel printMode=$printMode layout=${viewModel.currentLayout} geometry=${viewModel.currentGeometry}")
    val layout = viewModel.currentLayout ?: return
    val geometry = viewModel.currentGeometry ?: return
    log.d("KeyboardPanel showing $layout on $geometry")
    val showMultiLayers = viewModel.currentLayout?.isLayerMulti(viewModel.currentLayer) == true
            && viewModel.options.mode == Options.Mode.MODE_LAYOUT
    val mainLayer = if (showMultiLayers) viewModel.currentLayer + 1 else viewModel.currentLayer

    val mediaScaleFactor = if (printMode) 2f / LocalDensity.current.density else 1f
    log.d("KeyboardPanel mediaScaleFactor $mediaScaleFactor")
    val boxWidth = AppDimens.keySize.times(geometry.width).times(mediaScaleFactor)
    val boxHeight = AppDimens.keySize.times(geometry.height).times(mediaScaleFactor)
    val graphicsLayer = rememberGraphicsLayer()
    if (printMode) {
        LaunchedEffect(Unit) {
            delay(1.seconds)
            val screenshot = graphicsLayer.toImageBitmap()
            viewModel.onEvent(MainViewModel.KeyEventAction.ImagePreparedAction(screenshot))
        }
    }
    val mainModifier = if (printMode) {
        Modifier.drawWithContent {
            if (printMode) {
                // Calls record() to capture the content in the graphics layer
                graphicsLayer.record {
                    this@drawWithContent.drawContent()
                }
                drawLayer(graphicsLayer)
            }
        }
    } else {
        Modifier
    }
    Box(
        modifier = mainModifier.padding(horizontal = 10.dp)
            .padding(top = 10.dp)
            .size(boxWidth, boxHeight)
    ) {
        geometry.keys.forEach { key ->
            //special case - deal with split spacebar
            var visible = true
            if ("SPC" == key.id) {
                if (viewModel.options.showSplit) visible = false
            } else if ("LSPC" == key.id || "RSPC" == key.id) {
                if (!viewModel.options.showSplit) visible = false
            }

            val keyHighlight = viewModel.options.showStyles && key.highlight
            val keyGraphicResource =
                viewModel.getKeyGraphicResource(layout.getGraphic(mainLayer, key.id))
            val specifiedColor = layout.getColor(mainLayer, key.id)

            val keyMainLabel = when (viewModel.options.mode) {
                Options.Mode.MODE_LAYOUT -> layout.getLabel(mainLayer, key.id).formattedLabel()
                Options.Mode.MODE_SCORE -> if (key.score >= 0.0) key.score.formatToOneDecimalPlace() else ""
                Options.Mode.MODE_DISTANCE -> if (key.distance >= 0.0) key.distance.formatToOneDecimalPlace() else ""
                Options.Mode.MODE_KEYID -> key.id
            }
            log.d("key ${key.id} ${key.finger} [${key.x},${key.y}] = $keyMainLabel [$specifiedColor]")

            val alpha = viewModel.selectKeyAlpha(specifiedColor)

            val keyColor = viewModel.selectKeyColor(key, keyMainLabel, specifiedColor,
                viewModel.options)

            val backgroundColor = viewModel.options.keyRenderOption.background(keyHighlight)
            val textDecoration = viewModel.options.keyRenderOption.textHighlightStyle(keyHighlight)
            val isDragTarget = (viewModel.keyDragging != key && viewModel.keyDragDest == key)

            if (keyColor != null && visible) {
                val keyCornerLabels =
                    if (showMultiLayers) viewModel.getMultiLabels(layout, mainLayer, key) else null
                KeyPanel(
                    key = key,
                    options = viewModel.options,
                    geometryHeight = geometry.height,
                    keyColor = keyColor,
                    backgroundColor = backgroundColor,
                    foregroundImage = keyGraphicResource,
                    alpha = alpha,
                    mainLabel = keyMainLabel,
                    mainLabelDecoration = textDecoration,
                    cornerLabels = keyCornerLabels,
                    isDragTarget = isDragTarget,
                    printMode = printMode,
                    eventListener = viewModel::onEvent,
                )
            }
        }

    }

}