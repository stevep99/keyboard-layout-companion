package io.github.colemakmods.keyboard_companion.options

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import io.github.colemakmods.keyboard_companion.model.KeyType

interface KeyRenderOption {
    @Composable fun renderKeyBlock(width: Dp, height: Dp, keyColor: Color)
    fun selectColorByFinger(finger: Int): Color
    fun selectColorByScore(score: Double): Color
    fun selectColorByKeyType(keyType: KeyType): Color
    fun selectColorByKeySpec(colorSpec: Char): Color?
    fun textHighlightStyle(isHighlight: Boolean): TextDecoration?
    fun background(isHighlight: Boolean): Color?
}
