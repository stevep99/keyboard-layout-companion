package io.github.colemakmods.keyboard_companion.view

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import io.github.colemakmods.keyboard_companion.KeyboardCompanionApplication
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.model.Geometry
import io.github.colemakmods.keyboard_companion.model.GeometryInitializer
import io.github.colemakmods.keyboard_companion.model.Key
import io.github.colemakmods.keyboard_companion.model.KeyType
import io.github.colemakmods.keyboard_companion.model.Layout
import io.github.colemakmods.keyboard_companion.model.LayoutInitializer
import io.github.colemakmods.keyboard_companion.options.KeyRenderOption
import io.github.colemakmods.keyboard_companion.options.Options
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.Dispatcher
import timber.log.Timber
import java.io.PrintWriter
import java.util.UUID
import kotlin.math.roundToInt

class MainViewModel(application: Application): AndroidViewModel(application) {
    var showInfoDialog by mutableStateOf(false)
    var showSettingsDialog by mutableStateOf(false)
    val appVersionText = (application as KeyboardCompanionApplication).getVersionText()
    var options by mutableStateOf(Options())

    var layoutList: List<Layout> = arrayListOf()
        private set
    var geometryList: List<Geometry> = arrayListOf()
        private set
    var currentLayout by mutableStateOf<Layout?>(null)
        private set
    var currentGeometry by mutableStateOf<Geometry?>(null)
    var currentLayer by mutableIntStateOf(0)

    var drawRefreshState by mutableStateOf("")
        private set

    sealed interface KeyEventAction {
        class KeyDragAction(val dx: Float, val dy: Float): KeyEventAction
        object KeyClickAction: KeyEventAction
    }

    init {
        val gi = GeometryInitializer()
        viewModelScope.launch(Dispatchers.IO) {
            geometryList = gi.loadData(application, externalGeometryDir(application))
            if (geometryList.isEmpty()) {
                throw InitError("Unable to initialize geometry files")
            }
            Timber.d("Initialized ${geometryList.size} geometry files")

            val li = LayoutInitializer(geometryList)
            layoutList = li.loadData(application, externalLayoutDir(application))
            if (layoutList.isEmpty()) {
                throw InitError("Unable to initialize layout files")

            }
            Timber.d("Initialized ${layoutList.size} layout files")

            for (i in layoutList.indices) {
                if (layoutList[i].name == DEFAULT_LAYOUT_OPTION) {
                    updateCurrentLayout(layoutList[i])
                }
            }
            currentGeometry = currentLayout?.compatibleGeometries?.firstOrNull()
            Timber.d("Selected initial layout ${currentLayout?.name} with geometry ${currentGeometry?.title}")
        }
    }

    private fun externalGeometryDir(context: Context) = context.getExternalFilesDir("geometry")

    private fun externalLayoutDir(context: Context) = context.getExternalFilesDir("layout")

    fun updateKeyRenderOption(keyRenderOption: KeyRenderOption) {
        options = options.copy(keyRenderOption = keyRenderOption)
    }

    fun updateMode(mode: Options.Mode) {
        options = options.copy(mode = mode)
        if (options.mode === Options.Mode.MODE_DISPLAY) {
            currentLayout?.dumpLayout(PrintWriter(System.out))
        } else {
            currentGeometry?.updateDistancesScores()
        }
    }

    fun updateCurrentLayout(layout: Layout) {
        currentLayout = layout
        geometryList = if (options.mode === Options.Mode.MODE_DISPLAY) currentLayout?.compatibleGeometries ?: geometryList else geometryList
    }

    fun updateShowSplitOption(showSplit: Boolean) {
        options = options.copy(showSplit = showSplit)
    }

    fun updateShowStylesOption(showStyles: Boolean) {
        options = options.copy(showStyles = showStyles)
    }

    fun updateShowFingersOption(showFingers: Boolean) {
        options = options.copy(showFingers = showFingers)
    }

    fun updateKeyFilterOption(keyFilterOption: Options.KeyFilter) {
        options = options.copy(keyFilterOption = keyFilterOption)
    }

    fun layerCount() = currentLayout?.layerCount ?: 0

    fun currentLayerName() = currentLayout?.getLayerName(currentLayer) ?: ""
    fun nextLayer() {
        if (layerCount() > 0) {
            ++currentLayer
            if (currentLayer >= layerCount()) currentLayer = 0
        }
    }

    fun previousLayer() {
        if (layerCount() > 0) {
            --currentLayer
            if (currentLayer < 0) currentLayer = layerCount() - 1
        }
    }

    fun getMultiLabels(layout: Layout, layer: Int, key: Key): Array<String> {
        return arrayOf(
            layout.getLabel(layer + 1, key.keyId),
            layout.getLabel(layer + 2, key.keyId),
            layout.getLabel(layer + 3, key.keyId),
            layout.getLabel(layer + 4, key.keyId),
        )
    }

    fun selectKeyDrawable(key: Key, row: Geometry.ROW, label: String, color: String?, options: Options): Int {
        if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_LETTERS_ONLY
            && !label.matches(Regex(LETTERS_ONLY))) {
            return 0
        } else if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_LETTERS_PUNCTUATION
            && !label.matches(Regex(LETTERS_PUNCTUATION))) {
            return 0
        } else if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_MAIN_ZONE && key.type !== KeyType.MAIN_SECTION) {
            return 0
        } else if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_CHAR_KEYS && key.type === KeyType.NON_CHARACTER) {
            return 0
        } else if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_EXCLUDE_BOTTOM && row == Geometry.ROW.modifier_row) {
            return 0
        }
        var drawable = 0
        if (options.showFingers) {
            drawable = options.keyRenderOption.drawableByFinger(key.finger)
        } else {
            if (options.mode === Options.Mode.MODE_DISPLAY) {
                if (!color.isNullOrEmpty()) {
                    drawable = options.keyRenderOption.drawableByColorSpec(color[0])
                    if (drawable == KeyRenderOption.KEY_DRAWABLE_FINGER) {
                        drawable = options.keyRenderOption.drawableByFinger(key.finger)
                    }
                } else {
                    drawable = options.keyRenderOption.drawableByKeyType(key.type)
                }
            } else if (options.mode === Options.Mode.MODE_SCORE) {
                drawable = options.keyRenderOption.drawableByScore(Math.round(key.score * 10.0) / 10.0)
            } else if (options.mode === Options.Mode.MODE_DISTANCE) {
                drawable = options.keyRenderOption.drawableByScore(1.0 + key.distance * 1.5)
            }
        }
        return drawable
    }

    fun handleClick(key: Key) {
        key.toggleHighlight()
        drawRefreshState = UUID.randomUUID().toString()
    }

    fun handleDragDrop(key: Key, dx: Float, dy: Float) {
        Timber.d("drag moved ${key.keyId} by $dx $dy")
        val destX = (key.x + dx).roundToInt()
        val destRow = Geometry.ROW.values().getOrNull((key.y + dy).roundToInt()) ?: return
        val destKey = currentGeometry?.getKey(destRow, destX) ?: return

        Timber.d("drag valid from ${key.keyId} to ${destKey.keyId}")
        currentLayout?.switchKeys(currentLayer, key.keyId, destKey.keyId)
        drawRefreshState = UUID.randomUUID().toString()
    }

    fun formatLabel(label: String): String {
        return if (label.length == 1) {
            //show uppercase labels on alpha keys
            label.uppercase()
        } else {
            label
        }
    }

    fun formatValue(value: Double): String {
        return if (value >= 0.0) String.format("%.1f", value) else ""
    }

    fun getKeyGraphicResource(graphicId: String?): Int {
        return if (graphicId == null) 0 else when (graphicId) {
            "O" -> R.drawable.blackcircle
            "o" -> R.drawable.bluecircle
            "x" -> R.drawable.redx
            "*" -> R.drawable.star
            else -> 0
        }
    }

    fun selectKeyAlpha(color: String?): Float {
        if (color != null && color.length >= 2) {
            return when (color[0]) {
                'f' -> 0.5f
                else -> 1.0f
            }
        }
        return 1.0f
    }

    companion object {
        private const val DEFAULT_LAYOUT_OPTION = "Colemak-DH"
        private const val LETTERS_PUNCTUATION = "^[a-zA-Z.,/;']$"
        private const val LETTERS_ONLY = "^[a-zA-Z]$"
    }

    class InitError(message: String) : Exception(message)
}