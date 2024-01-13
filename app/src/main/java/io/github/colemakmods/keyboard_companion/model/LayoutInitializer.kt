package io.github.colemakmods.keyboard_companion.model

import android.content.Context
import io.github.colemakmods.keyboard_companion.model.Geometry.FingerConfig
import io.github.colemakmods.keyboard_companion.model.LayoutMapping.Companion.createLabelLayer
import io.github.colemakmods.keyboard_companion.model.LayoutMapping.LayoutMappingBuilder
import io.github.colemakmods.keyboard_companion.util.StringUtil
import timber.log.Timber
import java.io.*
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by steve on 15/07/15.
 */

class LayoutInitializer(val geometryList: List<Geometry>) {
    fun loadData(context: Context, layoutDir: File?): ArrayList<Layout> {
        val layouts = ArrayList<Layout>()

        //load in available layout files
        context.assets.list("layout")
                ?.filter { it.endsWith(".keyb") }
                ?.forEach { fileName ->
                    Timber.d("Loading layout file $fileName")
                    try {
                        context.assets.open("layout/$fileName").use {
                            layouts.add(
                                initLayout(it, fileName)
                            )
                        }
                    } catch (ex: IOException) {
                        Timber.w(ex, "Error reading layout file $fileName")
                    }
                }
        layoutDir?.list()
                ?.filter { it.endsWith(".keyb") }
                ?.forEach { fileName ->
                    Timber.d("Loading layout file $fileName")
                    try {
                        FileInputStream(File(layoutDir, fileName)).use {
                            layouts.add(
                                initLayout(it, fileName)
                            )

                        }
                    } catch (ex: IOException) {
                        Timber.w(ex, "Error reading layout file $fileName")
                    }
                }

        layouts.sort()

        //add basic layout with key IDs only
        val calcLayout = Layout(Layout.LAYOUT_KEYID, "template", "KeyID", geometryList, createLabelLayer())
        layouts.add(calcLayout)
        return layouts
    }

    @Throws(IOException::class)
    private fun initLayout(ins: InputStream, fileName: String): Layout {
        val reader = BufferedReader(InputStreamReader(ins))
        val defaultName = fileName.substring(0, fileName.lastIndexOf("."))
        return parseLayout(defaultName, reader)
    }

    @Throws(IOException::class)
    private fun parseLayout(defaultName: String, reader: BufferedReader): Layout {
        var name = defaultName
        var format: Array<Array<String>> = LayoutMapping.FORMAT_SIMPLE
        val layoutMappingBuilder = LayoutMappingBuilder()
        val compatibleGeometries: MutableList<Geometry> = ArrayList(geometryList)
        var row = 0
        var line: String?
        var id = ""
        var type = ""
        var mode = ""
        do {
            line = reader.readLine()
            if (line == null) break
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
                            val fingerConfig = FingerConfig.valueOf(configId)
                            compatibleGeometries.addAll(Geometry.findByFingers(geometryList, fingerConfig))
                            compatibleGeometries.sort()
                        } catch (ex: Exception) {
                            Timber.w(ex,"Invalid FingerConfig for $name:$line")
                        }
                    }
                } else if ("#mapping" == mode) {
                    if ("FORMAT_SIMPLE".equals(arg, ignoreCase = true)) {
                        format = LayoutMapping.FORMAT_SIMPLE
                    } else if ("FORMAT_FULL".equals(arg, ignoreCase = true)) {
                        format = LayoutMapping.FORMAT_FULL
                    } else {
                        Timber.w("Error: unknown mapping format $format")
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
        } while (line != null)
        val mapping = layoutMappingBuilder.toLayoutMapping()
        id = id.ifBlank { StringUtil.nameToId(name) }
        return Layout(id, type, name, compatibleGeometries, mapping)
    }


}