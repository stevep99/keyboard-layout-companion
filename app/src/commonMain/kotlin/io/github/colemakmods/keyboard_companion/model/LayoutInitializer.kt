package io.github.colemakmods.keyboard_companion.model

import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.platform.Common
import io.github.colemakmods.keyboard_companion.util.StringUtil
import keyboard_layout_companion.app.generated.resources.Res

private val log = Logger.withTag("LayoutInitializer")

private const val LAYOUT_MANIFEST_FILE = "files/layout/_manifest.txt"
//private const val LAYOUT_MANIFEST_FILE = "files/layout/_manifest_steve.txt"

class LayoutInitializer {
    private val layouts = ArrayList<Layout>()

    suspend fun loadData(geometryList: List<Geometry>): List<Layout> {
        log.d("loadData...")

        val layoutFileNames = Res.readBytes(LAYOUT_MANIFEST_FILE)
            .decodeToString()
            .lines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .map { "files/layout/$it".trim() }
        log.d("Layout manifest contains ${layoutFileNames.size} files")

        layoutFileNames.forEach { resourcePath ->
            log.d("Loading layout file $resourcePath")
            try {
                val fileBytes = Res.readBytes(resourcePath)
                layouts.add(
                    initLayout(
                        fileBytes.decodeToString(),
                        resourcePath.substringAfterLast('/'),
                        geometryList
                    )
                )
            } catch (ex: Exception) {
                log.w("Error loading layout resource $resourcePath", ex)
            }
        }
        Common.platform.loadExtraLayouts()?.let { extraFiles ->
            extraFiles.forEach {
                log.d("Loading extra layout file ${it.first}")
                try {
                    layouts.add(
                        initLayout(
                            it.second.decodeToString(),
                            it.first,
                            geometryList
                        )
                    )
                } catch (ex: Exception) {
                    log.w("Error loading extra layout resource ${it.first}", ex)
                }
            }
        }

        return layouts.sorted()
    }

    private fun initLayout(input: String, fileName: String, geometryList: List<Geometry>): Layout {
        val defaultName = fileName.substring(0, fileName.lastIndexOf("."))
        return parseLayout(defaultName, input, geometryList)
    }

    private fun parseLayout(defaultName: String, input: String, geometryList: List<Geometry>): Layout {
        var name = defaultName
        var format: Array<Array<String>> = LayoutMapping.FORMAT_SIMPLE
        val layoutMappingBuilder = LayoutMapping.LayoutMappingBuilder()
        val compatibleGeometries = geometryList.toMutableList()
        var preferGeometry = ""
        var id = ""
        var type = ""
        var mode = ""
        val lines = input.lines()
        var row = 0
        lines.forEach { line ->
            if (line.startsWith("#")) {
                val data = line.split(":".toRegex()).toTypedArray()
                mode = data[0].trim { it <= ' ' }
                val arg = if (data.size >= 2) data[1].trim { it <= ' ' } else ""
                if ("#id" == mode) {
                    id = arg
                } else if ("#type" == mode) {
                    type = arg
                } else if ("#layer" == mode) {
                    layoutMappingBuilder.addLayer(arg)
                } else if ("#multilayer" == mode) {
                    layoutMappingBuilder.addLayer(arg, true)
                } else if ("#name" == mode) {
                    name = arg.ifBlank { defaultName }
                } else if ("#config" == mode) {
                    compatibleGeometries.clear()
                    val configIds = arg.split(" +".toRegex()).toTypedArray()
                    for (configId in configIds) {
                        try {
                            val fingerConfig = Geometry.FingerConfig.valueOf(configId)
                            compatibleGeometries.addAll(
                                Geometry.filterByFingerConfig(geometryList, fingerConfig)
                            )
                            compatibleGeometries.sort()
                        } catch (ex: Exception) {
                            log.w("Invalid FingerConfig for $name:$line", ex)
                        }
                    }
                } else if ("#prefer_geometry" == mode) {
                    preferGeometry = arg
                } else if ("#mapping" == mode) {
                    if ("FORMAT_SIMPLE".equals(arg, ignoreCase = true)) {
                        format = LayoutMapping.FORMAT_SIMPLE
                    } else if ("FORMAT_MAIN".equals(arg, ignoreCase = true)) {
                        format = LayoutMapping.FORMAT_MAIN
                    } else if ("FORMAT_FULL".equals(arg, ignoreCase = true)) {
                        format = LayoutMapping.FORMAT_FULL
                    } else {
                        log.w("Error: unknown mapping format $format")
                    }
                }
                row = 0
            } else {
                //parse data
                if ("#labels" == mode) {
                    val tokens = line.trim { it <= ' ' }.split(" +".toRegex()).toTypedArray()
                    layoutMappingBuilder.addLabels(format, row, tokens)
                    ++row
                } else if ("#colors" == mode) {
                    val tokens = line.trim { it <= ' ' }.split(" +".toRegex()).toTypedArray()
                    layoutMappingBuilder.addColors(format, row, tokens)
                    ++row
                }
            }
        }
        val mapping = layoutMappingBuilder.toLayoutMapping()
        id = id.ifBlank { StringUtil.nameToId(name) }
        return Layout(id, type, name, compatibleGeometries, preferGeometry, mapping)
    }
}