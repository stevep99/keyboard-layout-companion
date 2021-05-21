package io.github.colemakmods.keyboard_companion.model

import android.content.Context
import io.github.colemakmods.keyboard_companion.view.KeyboardCompanionActivity.Companion.SD_GEOMETRY_DIR
import timber.log.Timber
import java.io.File
import java.io.FileInputStream
import java.io.IOException
import java.io.InputStream
import java.util.*

/**
 * Created by steve on 24/07/15.
 */
class GeometryInitializer {

    private val geometryList = ArrayList<Geometry>()

    fun init(context: Context) {
        context.assets.list("geometry")
                ?.filter { it.endsWith(".json") }
                ?.forEach { fileName ->
                    Timber.d("loading geometry file $fileName")
                    try {
                        context.assets.open("geometry/$fileName").use {
                            initGeometry(it)
                        }
                    } catch (ex: IOException) {
                        Timber.w(ex, "Error reading geometry file $fileName")
                    }
                }
        SD_GEOMETRY_DIR.list()
                ?.filter { it.endsWith(".json") }
                ?.forEach { fileName ->
                    Timber.d("loading geometry file $fileName")
                    try {
                        FileInputStream(File(SD_GEOMETRY_DIR, fileName)).use {
                            initGeometry(it)
                        }
                    } catch (ex: IOException) {
                        Timber.w(ex, "Error reading geometry file $fileName")
                    }
                }
        geometryList.sort()
    }

    @Throws(IOException::class)
    private fun initGeometry(ins: InputStream) {
        val geometry = Geometry.parse(ins)
        geometry.updateKeyIds()
        geometry.updateKeyCoordinates()
        geometry.updateHomePosition()
        geometryList.add(geometry)
    }

    fun getGeometryList(): List<Geometry> {
        return geometryList
    }

}