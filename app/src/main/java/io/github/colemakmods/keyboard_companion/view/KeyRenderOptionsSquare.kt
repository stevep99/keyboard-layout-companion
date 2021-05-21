package io.github.colemakmods.keyboard_companion.view

import android.graphics.Typeface
import android.text.style.CharacterStyle
import android.text.style.UnderlineSpan
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.model.Key
import io.github.colemakmods.keyboard_companion.model.KeyType

/**
 * Created by steve on 26/07/15.
 */
class KeyRenderOptionsSquare : KeyRenderOptions {

    override fun getKeyTypeface(): Typeface? = null

    override fun selectKeyDrawableByFinger(finger: Int): Int {
        return when (finger) {
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
    }

    override fun selectKeyDrawable(keyspec: String): Int {
        if (keyspec == "f") {
            return KeyRenderOptions.KEY_DRAWABLE_FINGER
        } else if (keyspec == "c") {
            return R.drawable.key_2_lc
        } else if (keyspec == "C") {
            return R.drawable.key_2_dc
        } else if (keyspec == "p") {
            return R.drawable.key_2_lp
        } else if (keyspec == "P") {
            return R.drawable.key_2_dp
        } else if (keyspec == "g") {
            return R.drawable.key_2_lg
        } else if (keyspec == "G") {
            return R.drawable.key_2_dg
        } else if (keyspec == "b") {
            return R.drawable.key_2_lb
        } else if (keyspec == "B") {
            return R.drawable.key_2_db
        } else if (keyspec == "y") {
            return R.drawable.key_2_ly
        } else if (keyspec == "Y") {
            return R.drawable.key_2_dy
        } else if (keyspec == "r") {
            return R.drawable.key_2_lr
        } else if (keyspec == "R") {
            return R.drawable.key_2_dr
        }
        return R.drawable.key_2_x
    }

    override fun selectKeyDrawableByScore(score: Double): Int {
        return if (score <= 0) {
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
    }

    override fun selectKeyDrawableByKeyType(keyType: KeyType): Int {
        return if (keyType === KeyType.MAIN_SECTION) {
            R.drawable.key_2_lg
        } else if (keyType === KeyType.NUMBER_ROW) {
            R.drawable.key_2_lb
        } else if (keyType === KeyType.NON_CHARACTER) {
            R.drawable.key_2_ly
        } else {
            R.drawable.key_2_x
        }
    }

    override fun getKeyBackground(highlight: Boolean): Int {
        return 0
    }

    override fun getTextHighlightStyle(highlight: Boolean): CharacterStyle? {
        return if (highlight) {
            UnderlineSpan()
        } else {
            null
        }
    }

}