package io.github.colemakmods.keyboard_companion.view

import android.graphics.Typeface
import android.text.style.CharacterStyle
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.model.KeyType
import io.github.colemakmods.keyboard_companion.model.Key

/**
 * Created by steve on 26/07/15.
 */
class KeyRenderOptionsRounded : KeyRenderOptions {

    override fun getKeyTypeface(): Typeface? = null

    override fun selectKeyDrawableByFinger(finger: Int): Int {
        return when (finger) {
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
    }

    override fun selectKeyDrawable(keyspec: String): Int {
        if (keyspec == "f") {
            return KeyRenderOptions.KEY_DRAWABLE_FINGER
        } else if (keyspec == "c") {
            return R.drawable.key_1_lc
        } else if (keyspec == "C") {
            return R.drawable.key_1_dc
        } else if (keyspec == "p") {
            return R.drawable.key_1_lp
        } else if (keyspec == "P") {
            return R.drawable.key_1_dp
        } else if (keyspec == "g") {
            return R.drawable.key_1_lg
        } else if (keyspec == "G") {
            return R.drawable.key_1_dg
        } else if (keyspec == "b") {
            return R.drawable.key_1_lb
        } else if (keyspec == "B") {
            return R.drawable.key_1_db
        } else if (keyspec == "y") {
            return R.drawable.key_1_ly
        } else if (keyspec == "Y") {
            return R.drawable.key_1_dy
        } else if (keyspec == "r") {
            return R.drawable.key_1_lr
        } else if (keyspec == "R") {
            return R.drawable.key_1_dr
        }
        return R.drawable.key_1_x
    }

    override fun selectKeyDrawableByScore(score: Double): Int {
        return if (score <= 0) {
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
    }

    override fun selectKeyDrawableByKeyType(keyType: KeyType): Int {
        return if (keyType === KeyType.MAIN_SECTION) {
            R.drawable.key_1_lg
        } else if (keyType === KeyType.NUMBER_ROW) {
            R.drawable.key_1_lb
        } else if (keyType === KeyType.NON_CHARACTER) {
            R.drawable.key_1_ly
        } else {
            R.drawable.key_1_x
        }
    }

    override fun getKeyBackground(highlight: Boolean): Int {
        return if (highlight) {
            -0x340000
        } else {
            0x00000000
        }
    }

    override fun getTextHighlightStyle(highlight: Boolean): CharacterStyle? = null

}