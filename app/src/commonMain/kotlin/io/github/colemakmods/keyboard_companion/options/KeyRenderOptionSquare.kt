package io.github.colemakmods.keyboard_companion.options

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import io.github.colemakmods.keyboard_companion.model.Key
import io.github.colemakmods.keyboard_companion.model.KeyType
import io.github.colemakmods.keyboard_companion.view.AppColor

/**
 * Created by steve on 26/07/15.
 */
object KeyRenderOptionSquare: KeyRenderOption {
    @Composable
    override fun renderKeyBlock(width: Dp, height: Dp, keyColor: Color) {
        Box(modifier = Modifier
            .size(width, height)
            .border(
                width = 1.5.dp,
                color = Color.DarkGray,
            )
            .background(
                color = keyColor,
            )
        ) {
            val radius = min(width, height)/6
            Box(modifier = Modifier
                .align(Alignment.Center)
                .fillMaxSize()
                .padding(radius)
                .background(
                    color = Color(0x33FFFFFF), //lighter
                    shape = RoundedCornerShape(radius),
                )
            )
        }
    }

    override fun selectColorByFinger(finger: Int): Color {
        return when (finger) {
            0,9 -> AppColor.keyLcColor
            1,8 -> AppColor.keyLpColor
            2,7 -> AppColor.keyLgColor
            3 -> AppColor.keyLbColor
            4,5 -> AppColor.keyLyColor
            6 -> AppColor.keyDbColor
            Key.FINGER_UNASSIGNED -> AppColor.keyLrColor
            else -> AppColor.keyXColor
        }
    }

    override fun selectColorByScore(score: Double): Color {
        return when {
            score <= 0 -> AppColor.keyXColor
            score < 1.5 -> AppColor.keyLgColor
            score < 2.0 -> AppColor.keyLcColor
            score < 2.5 -> AppColor.keyLbColor
            score < 3.0 -> AppColor.keyLpColor
            score < 4.0 -> AppColor.keyLrColor
            else -> AppColor.keyDrColor
        }
    }

    override fun selectColorByKeyType(keyType: KeyType): Color {
        return when(keyType) {
            KeyType.MAIN_SECTION -> AppColor.keyLgColor
            KeyType.NUMBER_ROW -> AppColor.keyLbColor
            KeyType.NON_CHARACTER -> AppColor.keyLyColor
            else -> AppColor.keyXColor
        }
    }

    override fun selectColorByKeySpec(colorSpec: Char): Color? {
        return when(colorSpec) {
            'f' -> null
            'c' -> AppColor.keyLcColor
            'C' -> AppColor.keyDcColor
            'p' -> AppColor.keyLpColor
            'P' -> AppColor.keyDpColor
            'g' -> AppColor.keyLgColor
            'G' -> AppColor.keyDgColor
            'b' -> AppColor.keyLbColor
            'B' -> AppColor.keyDbColor
            'y' -> AppColor.keyLyColor
            'Y' -> AppColor.keyDyColor
            'r' -> AppColor.keyLrColor
            'R' -> AppColor.keyDrColor
            else -> AppColor.keyXColor
        }
    }

    override fun textHighlightStyle(isHighlight: Boolean): TextDecoration? {
        return if (isHighlight) {
            TextDecoration.Underline
        } else {
            null
        }
    }

    override fun background(isHighlight: Boolean): Color? {
        return null
    }
}
