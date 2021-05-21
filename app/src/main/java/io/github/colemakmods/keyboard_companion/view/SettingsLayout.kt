package io.github.colemakmods.keyboard_companion.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.RadioButton
import io.github.colemakmods.keyboard_companion.R

class SettingsLayout(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    lateinit var settingsKeyGraphicRounded: RadioButton
    lateinit var settingsKeyGraphicSquare: RadioButton

    override fun onViewAdded(child: View?) {
        super.onViewAdded(child)
        settingsKeyGraphicRounded = findViewById(R.id.settingsKeyGraphicRounded)
        settingsKeyGraphicSquare = findViewById(R.id.settingsKeyGraphicSquare)
    }

    fun putOptions(options: Options) {
        if (options.keyRenderOptions is KeyRenderOptionsRounded) {
            settingsKeyGraphicRounded.isChecked = true
        } else if (options.keyRenderOptions is KeyRenderOptionsSquare) {
            settingsKeyGraphicSquare.isChecked = true
        }
    }

    fun getKeyGraphicSetting(): Class<out KeyRenderOptions> {
        if (settingsKeyGraphicRounded.isChecked) {
            return KeyRenderOptionsRounded::class.java
        } else {
            return KeyRenderOptionsSquare::class.java
        }
    }

}