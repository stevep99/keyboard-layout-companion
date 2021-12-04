package io.github.colemakmods.keyboard_companion.model

import timber.log.Timber
import java.util.*

/**
 * Created by steve on 30/03/16.
 */
class LayoutMapping(private val layers: List<Layer>) {

    companion object {
        private const val BLANK_LABEL = "_"
        private const val DEFAULT_LABEL = "\\0"

        val FORMAT_SIMPLE = arrayOf(
                arrayOf("AD01", "AD02", "AD03", "AD04", "AD05", "AD06", "AD07", "AD08", "AD09", "AD10", "AD11", "AD12", "BSL"),
                arrayOf("AC01", "AC02", "AC03", "AC04", "AC05", "AC06", "AC07", "AC08", "AC09", "AC10", "AC11", "AC12"),
                arrayOf("AB01", "AB02", "AB03", "AB04", "AB05", "AB06", "AB07", "AB08", "AB09", "AB10")
        )
        val FORMAT_FULL = arrayOf(
                arrayOf("AE00", "AE01", "AE02", "AE03", "AE04", "AE05", "AE06", "AE07", "AE08", "AE09", "AE10", "AE11", "AE12", "BKS"),
                arrayOf("TAB", "AD01", "AD02", "AD03", "AD04", "AD05", "AD06", "AD07", "AD08", "AD09", "AD10", "AD11", "AD12", "BSL"),
                arrayOf("CAP", "AC01", "AC02", "AC03", "AC04", "AC05", "AC06", "AC07", "AC08", "AC09", "AC10", "AC11", "AC12", "RET"),
                arrayOf("LSH", "AB01", "AB02", "AB03", "AB04", "AB05", "AB06", "AB07", "AB08", "AB09", "AB10", "RSH"),
                arrayOf("LCT", "LWIN", "LALT", "JROM", "LSPC", "SPC", "RSPC", "JHIR", "RALT", "RWIN", "MNU", "RCT")
        )
        val DEFAULT_LABELS = arrayOf(
                arrayOf("`", "1", "2", "3", "4", "5", "6", "7", "8", "9", "0", "-", "=", "⌫"),
                arrayOf("⇥", "Q", "W", "E", "R", "T", "Y", "U", "I", "O", "P", "[", "]", "\\"),
                arrayOf("⇪", "A", "S", "D", "F", "G", "H", "J", "K", "L", ";", "'", "#", "⏎"),
                arrayOf("⇧", "Z", "X", "C", "V", "B", "N", "M", ",", ".", "/", "⇧"),
                arrayOf("Ctrl", "⊞", "Alt", "", "", "", "", "", "Alt", "⊞", "☰", "Ctrl")
        )

        fun getKeyId(mapping: Array<Array<String>>, row: Int, col: Int): String? {
            return if (row < mapping.size && col < mapping[row].size) {
                mapping[row][col]
            } else {
                null
            }
        }

        @JvmStatic
        fun createLabelLayer(): LayoutMapping {
            val layers: MutableList<Layer> = ArrayList()
            val currentLayer = Layer("ID")
            currentLayer.labels = HashMap()
            layers.add(currentLayer)
            for (row in FORMAT_FULL.indices) {
                for (col in FORMAT_FULL[row].indices) {
                    val keyId = getKeyId(FORMAT_FULL, row, col)
                    currentLayer.putLabel(keyId, FORMAT_FULL[row][col])
                }
            }
            return LayoutMapping(layers)
        }
    }

    fun getLayerCount(): Int {
        return layers.size
    }

    fun getLayer(i: Int): Layer {
        return layers[i]
    }

    class Layer(val name: String, val isMulti: Boolean = false) {
        var labels: HashMap<String?, String?>? = null
        private var graphics: HashMap<String, String>? = null
        private var colors: HashMap<String, String>? = null

        fun putLabel(keyId: String?, label: String?) {
            if (labels == null) labels = HashMap()
            labels!![keyId] = label
        }

        fun getLabel(keyId: String?): String? {
            return if (labels == null) null else labels!![keyId]
        }

        fun putGraphic(keyId: String, graphic: String) {
            if (graphics == null) graphics = HashMap()
            graphics!![keyId] = graphic
        }

        fun getGraphic(keyId: String): String? {
            return if (graphics == null) null else graphics!![keyId]
        }

        fun putColor(keyId: String, color: String) {
            if (colors == null) colors = HashMap()
            colors!![keyId] = color
        }

        fun getColor(keyId: String): String? {
            return if (colors == null) null else colors!![keyId]
        }
    }

    class LayoutMappingBuilder {
        private val layers: MutableList<Layer> = ArrayList()

        fun addLayer(name: String?, isMulti: Boolean = false) {
            var layerName = name ?: "default"
            layers.add(Layer(layerName, isMulti))
        }

        fun addLabels(format: Array<Array<String>>, rowId: Int, row: Array<String>) {
            if (layers.size == 0) addLayer(null)
            val currentLayer = layers[layers.size - 1]
            for (col in row.indices) {
                val token = row[col]
                var label: String?
                var graphic: String? = null
                if (DEFAULT_LABEL != token) {
                    if (token.startsWith("\\") && token.length > 1) {
                        label = token.substring(1)
                    } else if (token == BLANK_LABEL) {
                        label = ""
                    } else if (token.contains(BLANK_LABEL)) {
                        val sep = token.indexOf(BLANK_LABEL)
                        if (sep < token.length) {
                            graphic = token.substring(sep + 1)
                        }
                        label = if (sep > 0) token.substring(0, sep) else ""
                    } else {
                        label = token
                    }
                    val keyId = getKeyId(format, rowId, col)
                    if (keyId != null) {
                        currentLayer.putLabel(keyId, label)
                        if (graphic != null) {
                            currentLayer.putGraphic(keyId, graphic)
                        }
                        Timber.d("labels: '${currentLayer.name}' : $keyId  $label")
                    }
                }
            }
        }

        fun addColors(format: Array<Array<String>>, rowId: Int, row: Array<String>) {
            if (layers.size == 0) addLayer(null)
            val currentLayer = layers[layers.size - 1]
            for (col in row.indices) {
                val color = row[col]
                if (BLANK_LABEL != color) {
                    val keyId = getKeyId(format, rowId, col)
                    if (keyId != null) {
                        currentLayer.putColor(keyId, color)
                        Timber.d( "labels: '${currentLayer.name}' : $keyId $color")
                    }
                }
            }
        }

        fun toLayoutMapping(): LayoutMapping {
            //fill any undefined keys with default mappings
            for (row in FORMAT_FULL.indices) {
                for (col in FORMAT_FULL[row].indices) {
                    val keyId = getKeyId(FORMAT_FULL, row, col)
                    val baseLayer = if (layers[0].isMulti) layers[1] else layers[0]
                    val baseLayerLabel = baseLayer.getLabel(keyId)
                    if (baseLayerLabel == null) {
                        baseLayer.putLabel(keyId, DEFAULT_LABELS[row][col])
                    }
                }
            }
            //return complete LayoutMapping object
            return LayoutMapping(layers)
        }
    }

}