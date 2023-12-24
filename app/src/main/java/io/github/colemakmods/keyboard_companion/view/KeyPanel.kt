package io.github.colemakmods.keyboard_companion.view

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGesturesAfterLongPress
import androidx.compose.foundation.layout.Box
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.integerResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextGeometricTransform
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.zIndex
import androidx.core.content.ContextCompat
import coil.compose.rememberAsyncImagePainter
import io.github.colemakmods.keyboard_companion.R
import timber.log.Timber
import kotlin.math.roundToInt

@Preview(name = "Preview1")
@Composable
fun KeyPanelPreview1() {
    KeyPanel(0.0f, 0.0f, 1.0f, R.drawable.key_1_db, Color.Transparent, 0, 1f,
        "H", null, arrayOf(")", "1", "⌫", "F1"))
}

@Preview(name = "Preview2")
@Composable
fun KeyPanelPreview2() {
    KeyPanel(0.0f, 0.0f, 1.0f, R.drawable.key_2_ly, Color.Transparent, 0, 1f, "A", TextDecoration.Underline)
}

@Preview(name = "Preview3")
@Composable
fun KeyPanelPreview3() {
    KeyPanel(0.0f, 0.0f, 1.0f, R.drawable.key_2_ly, Color.Transparent, R.drawable.bluecircle)
}

@Composable
fun KeyPanel(x: Float,
             y: Float,
             keyWidth: Float,
             @DrawableRes keyImage: Int,
             backgroundColor: Color,
             @DrawableRes foregroundImage: Int = 0,
             alpha: Float = 1f,
             mainLabel: String = "",
             mainLabelDecoration: TextDecoration? = null,
             cornerLabels: Array<String>? = null,
             printMode: Boolean = false,
             eventListener: (MainViewModel.KeyEventAction) -> Unit = {},
 ) {

    val keySizeDp = dimensionResource(if (printMode) R.dimen.key_size_print else R.dimen.key_size_disp)
    val width = keySizeDp.times(keyWidth)
    val height = keySizeDp
    val initX = with(LocalDensity.current) { keySizeDp.toPx() * x }
    val initY = with(LocalDensity.current) { keySizeDp.toPx() * y }
    var dragX by remember { mutableFloatStateOf( 0f ) }
    var dragY by remember { mutableFloatStateOf( 0f ) }
    var zIndex by remember { mutableFloatStateOf( 0f ) }
    val narrowTextGeometricTransform = TextGeometricTransform(scaleX = 0.8f)
    Box(modifier = Modifier
        .size(width, height)
        .offset {
            IntOffset((initX + dragX).roundToInt(), (initY + dragY).roundToInt())
        }
        .background(backgroundColor)
        .alpha(alpha)
        .zIndex(zIndex)
        .clickable { eventListener.invoke(MainViewModel.KeyEventAction.KeyClickAction) }
        .pointerInput(Unit) {
            detectDragGesturesAfterLongPress(
                onDragStart = {
                    Timber.d("onDragStart offset $it")
                    zIndex = 1f
                },
                onDragEnd = {
                    zIndex = 0f
                    val keyDeltaX = dragX / (keySizeDp.toPx())
                    val keyDeltaY = dragY / (keySizeDp.toPx())
                    eventListener.invoke(MainViewModel.KeyEventAction.KeyDragAction(keyDeltaX, keyDeltaY))
                    dragX = 0f
                    dragY = 0f
                },
                onDrag = { _, dragAmount ->
                    dragX += dragAmount.x
                    dragY += dragAmount.y
                }
            )
        }
    ) {
        Image(painter = rememberAsyncImagePainter(ContextCompat.getDrawable(LocalContext.current, keyImage)),
            "",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.size(width, height),
        )
        if (foregroundImage != 0) {
            Image(modifier = Modifier.size(width, height),
                painter = painterResource(id = foregroundImage),
                contentDescription = "")
        }
        val fontSizeSmall = with(LocalDensity.current) {
            dimensionResource(id = if (printMode) R.dimen.key_text_small_size_print else R.dimen.key_text_small_size_disp).toSp()
        }
        val smallHorizMargin = dimensionResource(if (printMode) R.dimen.key_text_small_margin_horiz_print else R.dimen.key_text_small_margin_horiz_disp)
        val smallVertMargin = dimensionResource(if (printMode) R.dimen.key_text_small_margin_vert_print else R.dimen.key_text_small_margin_vert_disp)

        cornerLabels?.get(0)?.let {
            Text(modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(smallHorizMargin, smallVertMargin),
                text = it,
                style = TextStyle(
                    textGeometricTransform = if (mainLabel.length > 1) narrowTextGeometricTransform else null
                ),
                color = colorResource(id = R.color.keyTextMinorColor),
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
                color = colorResource(id = R.color.keyTextMinorColor),
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
                color = colorResource(id = R.color.keyTextMinorColor),
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
                color = colorResource(id = R.color.keyTextMinorColor),
                fontSize = fontSizeSmall)
        }
        val isBold = mainLabel.length == 1 && mainLabel[0].code >= 0x2190 && mainLabel[0].code <= 0x21FF
        val fontSize = if (cornerLabels != null)
            dimensionResource(id = if (printMode) R.dimen.key_text_size_many_print else R.dimen.key_text_size_many_disp)
        else
            dimensionResource(id = if (printMode) R.dimen.key_text_size_single_print else R.dimen.key_text_size_single_disp)
        val blurRadius = integerResource(if (printMode) R.integer.key_text_shadow_size_print else R.integer.key_text_shadow_size_disp)
        Text(text = mainLabel,
            modifier = Modifier.align(Alignment.Center),
            style = TextStyle(
                textDecoration = mainLabelDecoration,
                shadow = Shadow(color = Color.White, blurRadius = blurRadius.toFloat()),
                textGeometricTransform = if (mainLabel.length > 1) narrowTextGeometricTransform else null
            ),
            color = colorResource(id = R.color.keyTextMainColor),
            fontSize = with(LocalDensity.current) {
                fontSize.toSp()
            },
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
        )
    }

}
