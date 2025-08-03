package io.github.colemakmods.keyboard_companion.view

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.model.GeometryInitializer
import io.github.colemakmods.keyboard_companion.model.LayoutInitializer
import io.github.colemakmods.keyboard_companion.model.Geometry
import io.github.colemakmods.keyboard_companion.model.Key
import io.github.colemakmods.keyboard_companion.model.KeyType
import io.github.colemakmods.keyboard_companion.model.Layout
import io.github.colemakmods.keyboard_companion.model.dumpAll
import io.github.colemakmods.keyboard_companion.model.dumpLayout
import io.github.colemakmods.keyboard_companion.options.KeyRenderOption
import io.github.colemakmods.keyboard_companion.options.Options
import io.github.colemakmods.keyboard_companion.platform.Common
import keyboard_layout_companion.app.generated.resources.Res
import keyboard_layout_companion.app.generated.resources.blackcircle
import keyboard_layout_companion.app.generated.resources.bluecircle
import keyboard_layout_companion.app.generated.resources.redx
import keyboard_layout_companion.app.generated.resources.star
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import kotlin.math.round
import kotlin.math.roundToInt
import kotlin.random.Random

private val log = Logger.withTag("MainViewModel")

class MainViewModel: ViewModel() {
    val appVersionText = Common.platform.getVersionText()
    var options by mutableStateOf(Options())

    lateinit var fullGeometryList: List<Geometry>

    var layoutList by mutableStateOf<List<Layout>>(emptyList())
        private set
    var geometryList by mutableStateOf<List<Geometry>>(emptyList())
        private set
    var currentLayout by mutableStateOf<Layout?>(null)
        private set
    var currentGeometry by mutableStateOf<Geometry?>(null)
        private set
    var currentLayer by mutableIntStateOf(0)
        private set
    var currentKey by mutableStateOf<Key?>(null)
        private set
    var keyDragging by mutableStateOf<Key?>(null)
        private set
    var keyDragDest by mutableStateOf<Key?>(null)
        private set
    var showInfoDialog by mutableStateOf(false)
        private set
    var infoDialogContent by mutableStateOf<String?>(null)
        private set
    var showSettingsDialog by mutableStateOf(false)
        private set
    var showTextOutputDialog by mutableStateOf(false)
        private set
    var textOutputContent by mutableStateOf<String?>(null)
        private set
    var printMode by mutableStateOf(false)
        private set
    var drawRefreshState by mutableStateOf(0)
        private set

    sealed interface KeyEventAction {
        data class SelectModeAction(val mode: Options.Mode): KeyEventAction
        data class SelectLayoutAction(val layout: Layout): KeyEventAction
        data class SelectGeometryAction(val geometry: Geometry): KeyEventAction
        data class SelectKeyAction(val key: Key?): KeyEventAction
        data class KeyEditConfirmAction(val labels: List<String?>, val finger: Int?, val highlight: Boolean): KeyEventAction
        data class KeyDragStartAction(val key: Key): KeyEventAction
        data class KeyDragAction(val dx: Float, val dy: Float): KeyEventAction
        data class KeyDragEndAction(val dx: Float, val dy: Float): KeyEventAction
        data class InfoDialogAction(val show: Boolean): KeyEventAction
        data class SettingsDialogAction(val show: Boolean): KeyEventAction
        data class TextOutputAction(val show: Boolean): KeyEventAction
        data object SaveImageAction: KeyEventAction
        data class ImagePreparedAction(val bitmap: ImageBitmap): KeyEventAction
    }

    init {
        log.d("Initialized ${geometryList.size} keyboard geometries")

        viewModelScope.launch(Dispatchers.Default) {
            val geometryInitializer = GeometryInitializer()
            fullGeometryList = geometryInitializer.loadData()
            if (fullGeometryList.isEmpty()) {
                throw InitError("Unable to initialize keyboard geometry files")
            }
            geometryList = fullGeometryList

            val layoutInitializer = LayoutInitializer()
            layoutList = layoutInitializer.loadData(geometryList)
            if (layoutList.isEmpty()) {
                throw InitError("Unable to initialize layout files")
            }

            log.d("Initialized ${geometryList.size} keyboard geometries and ${layoutList.size} layouts")

            layoutList.firstOrNull { it.name == DEFAULT_LAYOUT_OPTION }
                ?.let { updateCurrentLayout(it) } ?: updateCurrentLayout(layoutList.first())
            val initialGeometry = currentLayout?.compatibleGeometries?.firstOrNull { it.id == DEFAULT_GEOMETRY_OPTION }
                ?: currentLayout?.compatibleGeometries?.firstOrNull()
            initialGeometry?.let { updateCurrentGeometry(it) }
            log.d("Selected initial layout ${currentLayout?.name} with geometry ${currentGeometry?.title}")
        }
    }

    fun updateKeyRenderOption(keyRenderOption: KeyRenderOption) {
        options = options.copy(keyRenderOption = keyRenderOption)
    }

    fun updateModeSelectorVisible(modeSelectorVisible: Boolean) {
        options = options.copy(
            modeSelectorVisible = modeSelectorVisible,
            mode = Options.Mode.MODE_LAYOUT
        )
    }

    fun updateShowSplitOption(showSplit: Boolean) {
        options = options.copy(showSplit = showSplit)
    }

    fun updateShowStylesOption(showStyles: Boolean) {
        options = options.copy(showStyles = showStyles)
    }

    fun updateKeyColorSchemeOption(keyColorScheme: Options.KeyColorScheme) {
        options = options.copy(keyColorScheme = keyColorScheme)
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
            layout.getLabel(layer + 1, key.id),
            layout.getLabel(layer + 2, key.id),
            layout.getLabel(layer + 3, key.id),
            layout.getLabel(layer + 4, key.id),
        )
    }

    fun selectKeyColor(key: Key, label: String, specifiedColor: String?, options: Options): Color? {
        val row = key.y.roundToInt()
        if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_LETTERS_ONLY
            && !label.matches(Regex(LETTERS_ONLY))) {
            return null
        } else if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_LETTERS_PUNCTUATION
            && !label.matches(Regex(LETTERS_PUNCTUATION))) {
            return null
        } else if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_MAIN_ZONE && key.type !== KeyType.MAIN_SECTION) {
            return null
        } else if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_CHAR_KEYS && key.type === KeyType.NON_CHARACTER) {
            return null
        } else if (options.keyFilterOption == Options.KeyFilter.KEY_FILTER_EXCLUDE_BOTTOM && row == 0) {
            return null
        }
        var color: Color? = null
        if (options.keyColorScheme == Options.KeyColorScheme.FINGERS) {
            color = options.keyRenderOption.selectColorByFinger(key.finger)
        } else if (options.keyColorScheme == Options.KeyColorScheme.FUNCTION) {
            if (options.mode === Options.Mode.MODE_LAYOUT) {
                if (!specifiedColor.isNullOrEmpty()) {
                    color = options.keyRenderOption.selectColorByKeySpec(specifiedColor[0])
                    if (color == null) {
                        color = options.keyRenderOption.selectColorByFinger(key.finger)
                    }
                } else {
                    color = options.keyRenderOption.selectColorByKeyType(key.type)
                }
            } else if (options.mode === Options.Mode.MODE_SCORE) {
                color = options.keyRenderOption.selectColorByScore(round(key.score * 10.0) / 10.0)
            } else if (options.mode === Options.Mode.MODE_DISTANCE) {
                color = options.keyRenderOption.selectColorByScore(1.0 + key.distance * 1.5)
            }
        }
        return color
    }

    fun getKeyGraphicResource(graphicId: String?): DrawableResource? {
        return if (graphicId == null) null else when (graphicId) {
            "O" -> Res.drawable.blackcircle
            "o" -> Res.drawable.bluecircle
            "x" -> Res.drawable.redx
            "*" -> Res.drawable.star
            else -> null
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

    fun onEvent(event: KeyEventAction) {
        when (event) {
            is KeyEventAction.SelectModeAction -> {
                updateMode(event.mode)
            }
            is KeyEventAction.SelectGeometryAction -> {
                updateCurrentGeometry(event.geometry)
            }
            is KeyEventAction.SelectLayoutAction -> {
                updateCurrentLayout(event.layout)
            }
            is KeyEventAction.SelectKeyAction -> {
                log.d { "select key ${event.key}"}
                currentKey = event.key
            }
            is KeyEventAction.KeyEditConfirmAction -> {
                event.finger?.apply { currentKey?.finger = this }
                currentKey?.highlight = event.highlight
                if (event.highlight) {
                    currentGeometry?.keys?.forEach {
                        if (it.highlight && it.finger == event.finger && it.id != currentKey?.id) {
                            it.highlight = false
                        }
                    }
                }
                event.labels.forEachIndexed { index, label ->
                    val key = currentKey
                    if (key != null && label != null) {
                        currentLayout?.mapping?.getLayer(index)?.putLabel(key.id, label)
                    }
                }
                currentKey = null
                drawRefreshState = Random.nextInt()
            }
            is KeyEventAction.KeyDragStartAction -> {
                keyDragging = event.key
                keyDragDest = event.key
            }
            is KeyEventAction.KeyDragAction -> {
                keyDragging?.let {
                    handleDrag(it, event.dx, event.dy)
                }
            }
            is KeyEventAction.KeyDragEndAction -> {
                keyDragging?.let {
                    handleDragDrop(it, event.dx, event.dy)
                }
            }
            is KeyEventAction.InfoDialogAction -> {
                if (event.show) {
                    loadInfoDialogContent()
                }
                showInfoDialog = event.show
            }
            is KeyEventAction.SettingsDialogAction -> {
                showSettingsDialog = event.show
            }
            is KeyEventAction.TextOutputAction -> {
                if (event.show) {
                    onOutputText()
                }
                showTextOutputDialog = event.show
            }
            is KeyEventAction.SaveImageAction -> {
                printMode = true
            }
            is KeyEventAction.ImagePreparedAction -> {
                onImagePrepared(event.bitmap)
            }
        }
    }

    private fun updateMode(mode: Options.Mode) {
        log.d { "updateMode $mode"}
        options = options.copy(mode = mode)
        if (options.mode == Options.Mode.MODE_LAYOUT) {
            log.v {
                StringBuilder().apply {
                    currentLayout?.dumpLayout(this)
                }.toString()
            }
        }
    }

    private fun updateCurrentLayout(layout: Layout) {
        log.d { "updateCurrentLayout $layout" }
        currentLayout = layout
        // filter geometry list for compatibility with current layout
        geometryList = if (options.mode === Options.Mode.MODE_LAYOUT)
            currentLayout?.compatibleGeometries ?: fullGeometryList
        else
            fullGeometryList
        // select preferred geometry if defined
        if (layout.preferGeometryId.isNotEmpty()) {
            geometryList.firstOrNull { it.id == layout.preferGeometryId }?.apply {
                updateCurrentGeometry(this)
            }
        }
        if (!geometryList.contains(currentGeometry)) {
            updateCurrentGeometry(geometryList.first())
        }
        currentLayer = 0
    }

    private fun updateCurrentGeometry(geometry: Geometry) {
        log.d { "updateCurrentGeometry $geometry" }
        currentGeometry = geometry
        // set split setting if fixed by selected geometry
        if (geometry.split == Geometry.Split.NEVER || geometry.split == Geometry.Split.SPLITTABLE) {
            options = options.copy(showSplit = false)
        } else if (geometry.split == Geometry.Split.ALWAYS) {
            options = options.copy(showSplit = true)
        }
        drawRefreshState = Random.nextInt()
    }

    private fun handleDrag(key: Key, dx: Float, dy: Float) {
        //log.d("drag moved ${key.id} by $dx $dy")
        val destX = key.x + key.width / 2 + dx
        val destY = key.y + key.height / 2 - dy
        keyDragDest = currentGeometry?.getKey(destX, destY) ?: return
    }

    private fun handleDragDrop(key: Key, dx: Float, dy: Float) {
        log.d("drag moved ${key.id} by $dx $dy")
        val destX = key.x + key.width/2 + dx
        val destY = key.y + key.height/2 - dy
        val destKey = currentGeometry?.getKey(destX, destY) ?: return
        keyDragDest = null

        log.d("drag valid from ${key.id} to ${destKey.id}")
        currentLayout?.switchKeys(currentLayer, key.id, destKey.id)
        drawRefreshState = Random.nextInt()
    }

    private fun loadInfoDialogContent() {
        if (infoDialogContent != null) return
        log.d("Loading info dialog content")
        viewModelScope.launch {
            infoDialogContent = Res.readBytes("files/info.md").decodeToString()
            log.d("Loaded info dialog content, ${infoDialogContent?.length} bytes")
        }
    }

    /** Save an image of the currently selected keyboard */
    private fun onImagePrepared(bitmap: ImageBitmap) {
        log.d { "onImagePrepared ${bitmap.width}x${bitmap.height}"}
        getOutputImageFilename()?.also {
            viewModelScope.launch {
                Common.platform.saveImage(it, bitmap)
                printMode = false
            }
        }
    }

    private fun getOutputImageFilename(): String? {
        val currentLayout = this.currentLayout ?: return null
        val currentGeometry = this.currentGeometry ?: return null
        val split = if (options.showSplit) "_split" else ""
        return if (currentLayout.layerCount > 1) {
            val layerPart = currentLayout.getLayerName(currentLayer).replace("\\s+".toRegex(), "_")
                .lowercase()
            "${currentLayout.id}_${currentGeometry.id}${split}_$layerPart.png"
        } else {
            "${currentLayout.id}_${currentGeometry.id}$split.png"
        }
    }

    /** Generate a text representation of the currently selected keyboard */
    private fun onOutputText() {
        val currentLayout = this.currentLayout ?: return
        val currentGeometry = this.currentGeometry ?: return
        log.d { "onOutputText $currentLayout.id ${currentGeometry.id}"}
        val filename = "${currentLayout.id}_${currentGeometry.id}.dat"
        val content = StringBuilder().apply {
            currentLayout.dumpAll(this, currentGeometry)
        }.toString()
        Common.platform.outputTextKeyboard(filename, content)
        textOutputContent = content
    }

    companion object {
        private const val DEFAULT_LAYOUT_OPTION = "Qwerty"
        private const val DEFAULT_GEOMETRY_OPTION = "ansi_trad"
        private const val LETTERS_PUNCTUATION = "^[a-zA-Z.,/;']$"
        private const val LETTERS_ONLY = "^[a-zA-Z]$"
        val DRAG_DEST_HIGHLIGHT_COLOR = Color.Yellow.copy(alpha = 0.5f)
    }

    class InitError(message: String) : Exception(message)
}