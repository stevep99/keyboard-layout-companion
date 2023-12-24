package io.github.colemakmods.keyboard_companion.options

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextDecoration
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.model.Key
import io.github.colemakmods.keyboard_companion.model.KeyType

/**
 * Created by steve on 26/07/15.
 */
val keyRenderOptionSquare = KeyRenderOption(
    drawableByFinger = { finger -> 
        when (finger) {
            0 -> R.drawable.key_2_lc
            1 -> R.drawable.key_2_lp
            2 -> R.drawable.key_2_lg
            3 -> R.drawable.key_2_lb
            4 -> R.drawable.key_2_ly
            5 -> R.drawable.key_2_ly
            6 -> R.drawable.key_2_db
            7 -> R.drawable.key_2_dg
            8 -> R.drawable.key_2_dp
            9 -> R.drawable.key_2_dc
            Key.FINGER_UNASSIGNED -> R.drawable.key_2_lr
            else -> 0
        }
    },
    drawableByScore = { score ->
        if (score <= 0) {
            R.drawable.key_2_x
        } else if (score < 1.5) {
            R.drawable.key_2_lg
        } else if (score < 2.0) {
            R.drawable.key_2_lc
        } else if (score < 2.5) {
            R.drawable.key_2_lb
        } else if (score < 3.0) {
            R.drawable.key_2_lp
        } else if (score < 4.0) {
            R.drawable.key_2_lr
        } else {
            R.drawable.key_2_dr
        }
    },
    drawableByKeyType = { keyType ->
        if (keyType === KeyType.MAIN_SECTION) {
            R.drawable.key_2_lg
        } else if (keyType === KeyType.NUMBER_ROW) {
            R.drawable.key_2_lb
        } else if (keyType === KeyType.NON_CHARACTER) {
            R.drawable.key_2_ly
        } else {
            R.drawable.key_2_x
        }
    },
    drawableByColorSpec = { spec ->
        if (spec == 'f') {
            KeyRenderOption.KEY_DRAWABLE_FINGER
        } else if (spec == 'c') {
            R.drawable.key_2_lc
        } else if (spec == 'C') {
            R.drawable.key_2_dc
        } else if (spec == 'p') {
            R.drawable.key_2_lp
        } else if (spec == 'P') {
            R.drawable.key_2_dp
        } else if (spec == 'g') {
            R.drawable.key_2_lg
        } else if (spec == 'G') {
            R.drawable.key_2_dg
        } else if (spec == 'b') {
            R.drawable.key_2_lb
        } else if (spec == 'B') {
            R.drawable.key_2_db
        } else if (spec == 'y') {
            R.drawable.key_2_ly
        } else if (spec == 'Y') {
            R.drawable.key_2_dy
        } else if (spec == 'r') {
            R.drawable.key_2_lr
        } else if (spec == 'R') {
            R.drawable.key_2_dr
        } else {
            R.drawable.key_2_x
        }
    },
    textHighlightStyle = { highlight ->
        if (highlight) {
            TextDecoration.Underline
        } else {
            null
        }
    },
    background = { Color.Transparent },
)
