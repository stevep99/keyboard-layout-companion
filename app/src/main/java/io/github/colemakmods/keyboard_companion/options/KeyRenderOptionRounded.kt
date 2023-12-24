package io.github.colemakmods.keyboard_companion.options

import androidx.compose.ui.graphics.Color
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.model.Key
import io.github.colemakmods.keyboard_companion.model.KeyType

/**
 * Created by steve on 26/07/15.
 */
val keyRenderOptionRounded = KeyRenderOption(
    drawableByFinger = {
        when (it) {
            0 -> R.drawable.key_1_lc
            1 -> R.drawable.key_1_lp
            2 -> R.drawable.key_1_lg
            3 -> R.drawable.key_1_lb
            4 -> R.drawable.key_1_ly
            5 -> R.drawable.key_1_ly
            6 -> R.drawable.key_1_db
            7 -> R.drawable.key_1_lg
            8 -> R.drawable.key_1_lp
            9 -> R.drawable.key_1_lc
            Key.FINGER_UNASSIGNED -> R.drawable.key_1_lr
            else -> 0
        }
    },
    drawableByScore = { score ->
        if (score <= 0) {
            R.drawable.key_1_x
        } else if (score < 1.5) {
            R.drawable.key_1_lg
        } else if (score < 2.0) {
            R.drawable.key_1_lc
        } else if (score < 2.5) {
            R.drawable.key_1_lb
        } else if (score < 3.0) {
            R.drawable.key_1_lp
        } else if (score < 4.0) {
            R.drawable.key_1_lr
        } else {
            R.drawable.key_1_dr
        }
    },
    drawableByKeyType = { keyType ->
        if (keyType === KeyType.MAIN_SECTION) {
            R.drawable.key_1_lg
        } else if (keyType === KeyType.NUMBER_ROW) {
            R.drawable.key_1_lb
        } else if (keyType === KeyType.NON_CHARACTER) {
            R.drawable.key_1_ly
        } else {
            R.drawable.key_1_x
        }
    },
    drawableByColorSpec = { spec ->
        if (spec == 'f') {
            KeyRenderOption.KEY_DRAWABLE_FINGER
        } else if (spec == 'c') {
            R.drawable.key_1_lc
        } else if (spec == 'C') {
            R.drawable.key_1_dc
        } else if (spec == 'p') {
            R.drawable.key_1_lp
        } else if (spec == 'P') {
            R.drawable.key_1_dp
        } else if (spec == 'g') {
            R.drawable.key_1_lg
        } else if (spec == 'G') {
            R.drawable.key_1_dg
        } else if (spec == 'b') {
            R.drawable.key_1_lb
        } else if (spec == 'B') {
            R.drawable.key_1_db
        } else if (spec == 'y') {
            R.drawable.key_1_ly
        } else if (spec == 'Y') {
            R.drawable.key_1_dy
        } else if (spec == 'r') {
            R.drawable.key_1_lr
        } else if (spec == 'R') {
            R.drawable.key_1_dr
        } else {
            R.drawable.key_1_x
        }
    },
    textHighlightStyle = { null },
    background = { highlight ->
        if (highlight) {
            Color.Red
        } else {
            Color.Transparent
        }
    },
)
