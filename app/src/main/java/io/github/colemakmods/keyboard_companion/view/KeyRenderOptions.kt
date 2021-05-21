package io.github.colemakmods.keyboard_companion.view

import android.graphics.Typeface
import android.text.style.CharacterStyle
import io.github.colemakmods.keyboard_companion.model.KeyType

/**
 * Created by steve on 26/07/15.
 */
interface KeyRenderOptions {
    companion object {
        const val KEY_DRAWABLE_FINGER = -1
    }

    fun getKeyTypeface(): Typeface?
    fun selectKeyDrawableByFinger(finger: Int): Int
    fun selectKeyDrawableByScore(score: Double): Int
    fun selectKeyDrawableByKeyType(keyType: KeyType): Int
    fun selectKeyDrawable(keyspec: String): Int
    fun getKeyBackground(highlight: Boolean): Int
    fun getTextHighlightStyle(highlight: Boolean): CharacterStyle?

}