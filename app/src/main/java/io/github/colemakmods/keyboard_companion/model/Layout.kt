package io.github.colemakmods.keyboard_companion.model

import timber.log.Timber
import java.io.PrintWriter

/**
 * Created by steve on 15/07/15.
 */
class Layout(
        val id: String,
        val type: String,
        val name: String,
        val compatibleGeometries: List<Geometry>,
        val mapping: LayoutMapping
) : Comparable<Layout> {

    companion object {
        const val LAYOUT_KEYID = "KEYID"
    }

    val layerCount: Int
        get() = mapping.getLayerCount()


    fun isLayerMulti(layer: Int): Boolean {
        return mapping.getLayer(layer).isMulti
    }

    fun getLayerName(layer: Int): String {
        return mapping.getLayer(layer).name
    }

    fun getLabel(layer: Int, keyId: String): String {
        val label = mapping.getLayer(layer).getLabel(keyId)
        return label ?: ""
    }

    fun getGraphic(layer: Int, keyId: String): String? {
        return mapping.getLayer(layer).getGraphic(keyId)
    }

    fun getColor(layer: Int, keyId: String): String? {
        val kId = if (keyId == "RET2") "RET" else keyId
        return mapping.getLayer(layer).getColor(kId)
    }

    fun switchKeys(currentLayer: Int, keyId1: String?, keyId2: String?) {
        if (currentLayer >= 0) {
            val label1 = mapping.getLayer(currentLayer).getLabel(keyId1)
            val label2 = mapping.getLayer(currentLayer).getLabel(keyId2)
            Timber.d("switchKeys $keyId1 $label1 <-> $keyId2 $label2")
            mapping.getLayer(currentLayer).putLabel(keyId1, label2)
            mapping.getLayer(currentLayer).putLabel(keyId2, label1)
        }
    }

    override fun toString(): String {
        return name
    }

    fun dumpAll(writer: PrintWriter, geometry: Geometry) {
        dumpLayout(writer)
        dumpScores(writer, geometry)
        dumpFingers(writer, geometry)
    }

    fun dumpLayout(writer: PrintWriter) {
        println("Keyboard $name")
        for (row in LayoutMapping.FORMAT_FULL.indices) {
            for (col in LayoutMapping.FORMAT_FULL[row].indices) {
                val keyId = LayoutMapping.getKeyId(LayoutMapping.FORMAT_FULL, row, col)
                val ch = mapping.getLayer(0).getLabel(keyId)
                if (ch != null) writer.print(ch) else writer.print('□')
                writer.print(' ')
            }
            writer.println()
        }
    }

    fun dumpScores(writer: PrintWriter, geometry: Geometry) {
        writer.println("Effort:")
        for (row in LayoutMapping.FORMAT_FULL.indices) {
            for (col in LayoutMapping.FORMAT_FULL[row].indices) {
                val keyId = LayoutMapping.getKeyId(LayoutMapping.FORMAT_FULL, row, col)
                keyId?.let {
                    val key = geometry.getKey(keyId)
                    if (key != null) writer.print(String.format("%1.1f", key.score)) else writer.print("□.□")
                    writer.print(' ')
                }
            }
            writer.println()
        }
    }

    fun dumpFingers(writer: PrintWriter, geometry: Geometry) {
        println("Fingers:")
        for (row in LayoutMapping.FORMAT_FULL.indices) {
            for (col in LayoutMapping.FORMAT_FULL[row].indices) {
                val keyId = LayoutMapping.getKeyId(LayoutMapping.FORMAT_FULL, row, col)
                val key = geometry.getKey(keyId)
                if (key != null) writer.print(String.format("%1d", key.finger)) else writer.print("□")
                writer.print(' ')
            }
            writer.println()
        }
    }

    override fun compareTo(other: Layout): Int {
        val typeCompare = this.type.compareTo(other.type)
        return if (typeCompare == 0) this.name.compareTo(other.name) else typeCompare
    }

}