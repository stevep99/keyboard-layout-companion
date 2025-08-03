package io.github.colemakmods.keyboard_companion.view

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.model.Key
import io.github.colemakmods.keyboard_companion.options.KeyRenderOptionSquare
import io.github.colemakmods.keyboard_companion.view.MainViewModel.KeyEventAction.*
import io.github.colemakmods.keyboard_companion.options.Options
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.bluecircle
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import kotlin.math.roundToInt

private val log = Logger.withTag("KeyPanel")

@Preview
@Composable
fun KeyPanelPreview1() {
    KeyPanel(
        Key("?",0.0f, 0.0f, 1.0f, 1.0f, 4),
        Options(),
        1.0f,
        AppColor.keyDbColor, Color.Transparent, null, 1f,
        "H", null, arrayOf(")", "1", "⌫", "F1"))
}

@Preview
@Composable
fun KeyPanelPreview2() {
    KeyPanel(
        Key("?", 0.0f, 0.0f, 1.0f, 1.0f, 2),
        Options(),
        1.0f,
        AppColor.keyDyColor, Color.Transparent, null, 1f,
        "A", TextDecoration.Underline)
}

@Preview
@Composable
fun KeyPanelPreview3() {
    KeyPanel(
        Key("?", 0.0f, 0.0f, 1.0f, 1.0f, 1),
        Options(keyRenderOption = KeyRenderOptionSquare),
        1.0f,
        AppColor.keyLyColor, Color.Transparent, Res.drawable.bluecircle)
}

@Composable
fun KeyPanel(key: Key,
             options: Options,
             geometryHeight: Float,
             keyColor: Color,
             backgroundColor: Color? = null,
             foregroundImage: DrawableResource? = null,
             alpha: Float = 1f,
             mainLabel: String = "",
             mainLabelDecoration: TextDecoration? = null,
             cornerLabels: Array<String>? = null,
             isDragTarget: Boolean = false,
             printMode: Boolean = false,
             eventListener: (MainViewModel.KeyEventAction) -> Unit = {},
 ) {

    val xp = if (options.showSplit && key.finger >= 5) key.x + 1.0f else key.x
    val yp = geometryHeight - (key.y + 1f)

    //log.d("KeyPanel $mainLabel $keyColor $backgroundColor $foregroundImage $alpha")
    val mediaScaleFactor = if (printMode) 2f / LocalDensity.current.density else 1f
    //log.d("mediaScaleFactor $mediaScaleFactor")
    val keySizeDp = AppDimens.keySize.times(mediaScaleFactor)
    val width = keySizeDp.times(key.width)
    val height = keySizeDp.times(key.height)
    val initX = with(LocalDensity.current) { keySizeDp.toPx() * xp }
    val initY = with(LocalDensity.current) { keySizeDp.toPx() * yp }
    var dragX by remember { mutableFloatStateOf( 0f ) }
    var dragY by remember { mutableFloatStateOf( 0f ) }
    var zIndex by remember { mutableFloatStateOf( 0f ) }
    val narrowTextGeometricTransform = TextGeometricTransform(scaleX = 0.8f)
    Box(modifier = Modifier
        .size(width, height)
        .offset {
            IntOffset((initX + dragX).roundToInt(), (initY + dragY).roundToInt())
        }
        .background(backgroundColor ?: Color.Transparent)
        .alpha(alpha)
        .zIndex(zIndex)
        .clickable { eventListener(SelectKeyAction(key)) }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    log.d("onDragStart offset $it")
                    zIndex = 1f
                    dragX = 0f
                    dragY = 0f
                    eventListener(KeyDragStartAction(key))
                },
                onDrag = { _, dragAmount ->
                    dragX += dragAmount.x
                    dragY += dragAmount.y
                    val keyDeltaX = dragX / keySizeDp.toPx()
                    val keyDeltaY = dragY / keySizeDp.toPx()
                    log.d("onDrag offset $keyDeltaX, $keyDeltaY")
                    eventListener(KeyDragAction(keyDeltaX, keyDeltaY))
                },
                onDragEnd = {
                    zIndex = 0f
                    val keyDeltaX = dragX / keySizeDp.toPx()
                    val keyDeltaY = dragY / keySizeDp.toPx()
                    log.d("onDragEnd offset $keyDeltaX, $keyDeltaY")
                    eventListener(KeyDragEndAction(keyDeltaX, keyDeltaY))
                    dragX = 0f
                    dragY = 0f
                },
            )
        }
    ) {

        options.keyRenderOption.renderKeyBlock(width, height, keyColor)

        if (foregroundImage != null) {
            Image(modifier = Modifier.size(width, height),
                painter = painterResource(foregroundImage),
                contentDescription = "")
        }
        val fontSizeSmall: TextUnit = AppDimens.keyTextSmallSize.times(mediaScaleFactor)
        val smallHorizMargin = AppDimens.keyTextSmallMarginHoriz.times(mediaScaleFactor)
        val smallVertMargin = AppDimens.keyTextSmallMarginVert.times(mediaScaleFactor)

        cornerLabels?.get(0)?.let {
            Text(modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(smallHorizMargin, smallVertMargin),
                text = it,
                style = TextStyle(
                    textGeometricTransform = if (mainLabel.length > 1) narrowTextGeometricTransform else null
                ),
                color = AppColor.keyTextMinorColor,
                fontSize = fontSizeSmall)
        }
        cornerLabels?.get(1)?.let {
            Text(modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(smallHorizMargin, smallVertMargin),
                text = it,
                style = TextStyle(
                    textGeometricTransform = if (mainLabel.length > 1) narrowTextGeometricTransform else null
                ),
                color = AppColor.keyTextMinorColor,
                fontSize = fontSizeSmall)
        }
        cornerLabels?.get(2)?.let {
            Text(modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(smallHorizMargin, smallVertMargin),
                text = it,
                style = TextStyle(
                    textGeometricTransform = if (mainLabel.length > 1) narrowTextGeometricTransform else null
                ),
                color = AppColor.keyTextMinorColor,
                fontSize = fontSizeSmall)
        }
        cornerLabels?.get(3)?.let {
            Text(modifier = Modifier
                .align(Alignment.TopStart)
                .padding(smallHorizMargin, smallVertMargin),
                text = it,
                style = TextStyle(
                    textGeometricTransform = if (mainLabel.length > 1) narrowTextGeometricTransform else null
                ),
                color = AppColor.keyTextMinorColor,
                fontSize = fontSizeSmall)
        }
        val isBold = mainLabel.length == 1 && mainLabel[0].code >= 0x2190 && mainLabel[0].code <= 0x21FF
        var fontSize = if (cornerLabels != null) AppDimens.keyTextSizeMany.times(mediaScaleFactor)
                else AppDimens.keyTextSizeSingle.times(mediaScaleFactor)
        val blurRadius = AppDimens.keyTextShadowSize.times(mediaScaleFactor)
        Text(text = mainLabel,
            modifier = Modifier.align(Alignment.Center).padding(1.dp.times(mediaScaleFactor)),
            style = TextStyle(
                textDecoration = mainLabelDecoration,
                shadow = Shadow(color = Color.White, blurRadius = blurRadius),
                textGeometricTransform = if (mainLabel.length > 1) narrowTextGeometricTransform else null
            ),
            color = AppColor.keyTextMainColor,
            fontSize = fontSize,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            onTextLayout = { r: TextLayoutResult ->
                if (r.didOverflowHeight) {
                    fontSize *= .9f
                }
            },
            maxLines = 1,
        )

        if (isDragTarget) {
            Box(modifier = Modifier
                .fillMaxSize()
                .background(MainViewModel.DRAG_DEST_HIGHLIGHT_COLOR)
            )
        }

    }

}
