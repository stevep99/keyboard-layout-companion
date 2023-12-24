package io.github.colemakmods.keyboard_companion.options

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import io.github.colemakmods.keyboard_companion.model.KeyType

data class KeyRenderOption(
    val drawableByFinger: (Int) -> Int,
    val drawableByScore: (Double) -> Int,
    val drawableByKeyType: (KeyType) -> Int,
    val drawableByColorSpec: (Char) -> Int,
    val textHighlightStyle: (Boolean) -> TextDecoration?,
    val background: (Boolean) -> Color,
) {
    companion object {
        const val KEY_DRAWABLE_FINGER = -1
    }
}

