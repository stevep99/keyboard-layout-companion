package io.github.colemakmods.keyboard_companion.view

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.view.DragEvent
import android.view.LayoutInflater
import android.view.View
import android.view.View.DragShadowBuilder
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import io.github.colemakmods.keyboard_companion.*
import io.github.colemakmods.keyboard_companion.model.Geometry
import io.github.colemakmods.keyboard_companion.model.Geometry.ROW
import io.github.colemakmods.keyboard_companion.model.Key
import io.github.colemakmods.keyboard_companion.model.KeyType
import io.github.colemakmods.keyboard_companion.model.Layout
import timber.log.Timber
import java.util.*

class KeyboardViewCreator(private val context: Context, private val onRefreshRequest: (() -> Unit)?) {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    companion object {
        private const val LETTERS_PUNCTUATION = "^[a-zA-Z.,/;']$"
        private const val LETTERS_ONLY = "^[a-zA-Z]$"
        private const val KEY_FILTER_ALL = 0
        private const val KEY_FILTER_EXCLUDE_BOTTOM = 1
        private const val KEY_FILTER_CHAR_KEYS = 2
        private const val KEY_FILTER_MAIN_ZONE = 3
        private const val KEY_FILTER_LETTERS_PUNCTUATION = 4
        private const val KEY_FILTER_LETTERS_ONLY = 5
    }

    fun createKeyViews(keyboardView: LinearLayout, geometry: Geometry, layout: Layout,
                       layer: Int, showMultiLayers: Boolean, isPrint: Boolean, options: Options) {

        val keyLayoutResource = selectKeyLayoutResource(isPrint, showMultiLayers)

        keyboardView.removeAllViews()
        for (row in ROW.values()) {
            val rowLayout = LinearLayout(context)
            var visibleInRow = 0
            var xDisplay = 0.0
            for (i in 0 until geometry.getRowLength(row)) {
                val key = geometry.getKey(row, i) ?: continue
                val keyView = layoutInflater.inflate(keyLayoutResource, null) as ViewGroup
                val keyButtonImage = keyView.findViewById<ImageView>(R.id.keyButtonImage)
                val keyGraphicImage = keyView.findViewById<ImageView>(R.id.keyGraphicImage)
                val keyButtonParams = keyButtonImage.layoutParams
                val keyViewWidth = keyButtonParams.width

                //special case - deal with split spacebar
                if ("SPC" == key.keyId) {
                    keyView.visibility = if (options.showSplit) View.GONE else View.VISIBLE
                } else if ("LSPC" == key.keyId || "RSPC" == key.keyId) {
                    keyView.visibility = if (options.showSplit) View.VISIBLE else View.GONE
                }

                //key width
                keyButtonParams.width = (key.width * keyViewWidth).toInt()
                keyButtonImage.layoutParams = keyButtonParams

                //determine main key label and graphics
                var keyLabel = layout.getLabel(layer, key.keyId)
                val keyGraphicResource = getKeyGraphicResource(layout.getGraphic(layer, key.keyId))
                if (keyLabel.length == 1) {
                    //show uppercase labels for alpha keys
                    keyLabel = keyLabel.uppercase(Locale.getDefault())
                }
                val color = layout.getColor(layer, key.keyId)
                Timber.d("row $row col $i key_disp ${key.keyId} ${key.finger}  = $keyLabel [$color]")

                //select the correct key background image
                val drawable = selectKeyDrawable(key, row, keyLabel, color, options)
                val alpha = selectKeyAlpha(color)
                if (drawable == 0) {
                    keyButtonImage.visibility = View.INVISIBLE
                } else {
                    ++visibleInRow
                    keyButtonImage.setBackgroundResource(drawable)
                    val keyMainTextView = keyView.findViewById<TextView>(R.id.keyMainTextView)
                    val keyHighlight = options.showStyles && key.highlight
                    if (options.showStyles) {
                        keyView.alpha = alpha
                    }
                    if (options.mode === Options.Mode.MODE_DISPLAY) {
                        showKeyLabel(layout, layer, showMultiLayers, key, keyView,
                                keyGraphicImage, keyMainTextView,
                                keyHighlight, keyLabel, keyGraphicResource, options.keyRenderOptions)
                    } else if (options.mode === Options.Mode.MODE_SCORE) {
                        showKeyValue(key.score, keyMainTextView, options.keyRenderOptions)
                    } else if (options.mode === Options.Mode.MODE_DISTANCE) {
                        showKeyValue(key.distance, keyMainTextView, options.keyRenderOptions)
                    }
                    val backgroundColor = options.keyRenderOptions.getKeyBackground(keyHighlight)
                    keyView.setBackgroundColor(backgroundColor)
                }

                //set a left margin if required
                //for split keyboards, add a gap between the left and right halves
                val params = LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                var x = key.x
                if (options.showSplit && key.finger >= 5) x += 1.0
                val leftOffset = x - xDisplay
                params.leftMargin = (keyViewWidth * leftOffset).toInt()
                if (keyView.visibility == View.VISIBLE) {
                    xDisplay += key.width + leftOffset
                }
                keyView.layoutParams = params
                keyView.setOnClickListener {
                    key.toggleHighlight();
                    onRefreshRequest?.invoke()
                }
                keyView.setOnLongClickListener { view: View ->
                    val shadowBuilder = DragShadowBuilder(keyView)
                    view.startDrag(null, shadowBuilder, view.tag, 0)
                    true
                }
                keyView.setOnDragListener { view: View, dragEvent: DragEvent ->
                    if (dragEvent.action == DragEvent.ACTION_DRAG_STARTED) {
                        Timber.d( "ACTION_DRAG_STARTED $view.tag")
                        return@setOnDragListener true
                    } else if (dragEvent.action == DragEvent.ACTION_DROP) {
                        Timber.d("ACTION_DROP $view.tag")
                        if (view.tag is Key) {
                            val destKey = view.tag as Key
                            val srcKey = dragEvent.localState as Key
                            Timber.d("swap keys ${srcKey.keyId}  <->  ${destKey.keyId}")
                            if (!showMultiLayers) {
                                layout.switchKeys(layer, srcKey.keyId, destKey.keyId)
                            }
                            onRefreshRequest?.invoke()
                        }
                        return@setOnDragListener true
                    }
                    false
                }
                keyView.tag = key
                rowLayout.addView(keyView)
            }

            //add the current row to the layout (assuming it contains some keys)
            if (visibleInRow > 0) {
                val rowParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                rowLayout.orientation = LinearLayout.HORIZONTAL
                keyboardView.addView(rowLayout, rowParams)
            }
        }
    }

    private fun selectKeyLayoutResource(isPrint: Boolean, showMultiLayers: Boolean): Int {
        return if (isPrint) {
            if (showMultiLayers) R.layout.key_print_many else R.layout.key_print_single
        } else {
            if (showMultiLayers) R.layout.key_disp_many else R.layout.key_disp_single
        }
    }

    private fun showKeyValue(value: Double, keyMainTextView: TextView, keyRenderOptions: KeyRenderOptions) {
        val keyTypeface = keyRenderOptions.getKeyTypeface()
        if (keyTypeface != null) {
            keyMainTextView.typeface = keyTypeface
        }
        if (value >= 0.0) {
            keyMainTextView.text = String.format("%.1f", value)
        }
        //keyTextView.setTextScaleX(0.8f);
    }

    private fun showKeyLabel(layout: Layout, layer: Int, showMultiLayers: Boolean, key: Key, keyView: ViewGroup,
                             keyGraphicImage: ImageView, keyMainTextView: TextView,
                             keyHighlight: Boolean, mainLabel: String, graphicResource: Int, keyRenderOptions: KeyRenderOptions) {
        val keyTypeface = keyRenderOptions.getKeyTypeface()
        val isBold = mainLabel.length == 1 && mainLabel[0].code >= 0x2190 && mainLabel[0].code <= 0x21FF
        keyMainTextView.setTypeface(keyTypeface, if (isBold) Typeface.BOLD else Typeface.NORMAL)
        keyGraphicImage.setImageResource(graphicResource)
        if (mainLabel.length > 1) keyMainTextView.textScaleX = 0.8f
        val characterStyle = keyRenderOptions.getTextHighlightStyle(keyHighlight)
        if (characterStyle != null) {
            val content = SpannableString(mainLabel)
            content.setSpan(characterStyle, 0, content.length, 0)
            keyMainTextView.text = content
        } else {
            keyMainTextView.text = mainLabel
        }
        if (showMultiLayers) {
            if (layer + 1 < layout.layerCount) {
                val tv = keyView.findViewById<TextView>(R.id.keyBottomRightTextView)
                val label = layout.getLabel(layer + 1, key.keyId)
                if (label.length > 1) tv.textScaleX = 0.8f
                tv.text = label
            }
            if (layer + 2 < layout.layerCount) {
                val tv = keyView.findViewById<TextView>(R.id.keyTopRightTextView)
                val label = layout.getLabel(layer + 2, key.keyId)
                if (label.length > 1) tv.textScaleX = 0.8f
                tv.text = label
            }
            if (layer + 3 < layout.layerCount) {
                val tv = keyView.findViewById<TextView>(R.id.keyBottomLeftTextView)
                val label = layout.getLabel(layer + 3, key.keyId)
                if (label.length > 1) tv.textScaleX = 0.8f
                tv.text = label
            }
            if (layer + 4 <= layout.layerCount) {
                val tv = keyView.findViewById<TextView>(R.id.keyTopLeftTextView)
                val label = layout.getLabel(layer + 4, key.keyId)
                if (label.length > 1) tv.textScaleX = 0.8f
                tv.text = label
            }
        }
    }

    private fun getKeyGraphicResource(graphicId: String?): Int {
        return if (graphicId == null) 0 else when (graphicId) {
            "O" -> R.drawable.blackcircle
            "o" -> R.drawable.bluecircle
            "x" -> R.drawable.redx
            "*" -> R.drawable.star
            else -> 0
        }
    }

    private fun selectKeyDrawable(key: Key, row: ROW, label: String, color: String?, options: Options): Int {
        if (options.keyFilterOption == KEY_FILTER_LETTERS_ONLY && !label.matches(Regex(LETTERS_ONLY))) {
            return 0
        } else if (options.keyFilterOption == KEY_FILTER_LETTERS_PUNCTUATION && !label.matches(Regex(LETTERS_PUNCTUATION))) {
            return 0
        } else if (options.keyFilterOption == KEY_FILTER_MAIN_ZONE && key.type !== KeyType.MAIN_SECTION) {
            return 0
        } else if (options.keyFilterOption == KEY_FILTER_CHAR_KEYS && key.type === KeyType.NON_CHARACTER) {
            return 0
        } else if (options.keyFilterOption == KEY_FILTER_EXCLUDE_BOTTOM && row == ROW.modifier_row) {
            return 0
        }
        var drawable = 0
        if (options.showFingers) {
            drawable = options.keyRenderOptions.selectKeyDrawableByFinger(key.finger)
        } else {
            if (options.mode === Options.Mode.MODE_DISPLAY) {
                if (!color.isNullOrEmpty()) {
                    drawable = options.keyRenderOptions.selectKeyDrawable(color[0])
                    if (drawable == KeyRenderOptions.KEY_DRAWABLE_FINGER) {
                        drawable = options.keyRenderOptions.selectKeyDrawableByFinger(key.finger)
                    }
                } else {
                    drawable = options.keyRenderOptions.selectKeyDrawableByKeyType(key.type)
                }
            } else if (options.mode === Options.Mode.MODE_SCORE) {
                drawable = options.keyRenderOptions.selectKeyDrawableByScore(Math.round(key.score * 10.0) / 10.0)
            } else if (options.mode === Options.Mode.MODE_DISTANCE) {
                drawable = options.keyRenderOptions.selectKeyDrawableByScore(1.0 + key.distance * 1.5)
            }
        }
        return drawable
    }

    private fun selectKeyAlpha(color: String?): Float {
        if (color != null && color.length >= 2) {
            return when (color[0]) {
                'f' -> 0.5f
                else -> 1.0f
            }
        }
        return 1.0f
    }

}