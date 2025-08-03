package io.github.colemakmods.keyboard_companion.model

import co.touchlab.kermit.Logger

private val log = Logger.withTag("Layout")

/**
 * Created by steve on 15/07/15.
 */
class Layout(
        val id: String,
        val type: String,
        val name: String,
        val compatibleGeometries: List<Geometry>,
        val preferGeometryId: String,
        val mapping: LayoutMapping
) : Comparable<Layout> {

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
            log.d("switchKeys $keyId1 $label1 <-> $keyId2 $label2")
            mapping.getLayer(currentLayer).putLabel(keyId1, label2)
            mapping.getLayer(currentLayer).putLabel(keyId2, label1)
        }
    }

    override fun toString(): String {
        return name
    }

    override fun compareTo(other: Layout): Int {
        val typeCompare = this.type.compareTo(other.type)
        return if (typeCompare == 0) this.name.uppercase().compareTo(other.name.uppercase()) else typeCompare
    }

}