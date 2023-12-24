package io.github.colemakmods.keyboard_companion.model

import android.content.Context
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

    fun loadData(context: Context, geometryDir: File?): List<Geometry> {
        val geometryList = ArrayList<Geometry>()
        context.assets.list("geometry")
                ?.filter { it.endsWith(".json") }
                ?.forEach { fileName ->
                    Timber.d("loading geometry file $fileName")
                    try {
                        context.assets.open("geometry/$fileName").use {
                            geometryList.add(
                                initGeometry(it)
                            )
                        }
                    } catch (ex: IOException) {
                        Timber.w(ex, "Error reading geometry file $fileName")
                    }
                }
        geometryDir?.list()
                ?.filter { it.endsWith(".json") }
                ?.forEach { fileName ->
                    Timber.d("loading geometry file $fileName")
                    try {
                        FileInputStream(File(geometryDir, fileName)).use {
                            geometryList.add(
                                initGeometry(it)
                            )
                        }
                    } catch (ex: IOException) {
                        Timber.w(ex, "Error reading geometry file $fileName")
                    }
                }
        geometryList.sort()
        return geometryList
    }

    @Throws(IOException::class)
    private fun initGeometry(ins: InputStream): Geometry {
        val geometry = Geometry.parse(ins)
        geometry.updateKeyIds()
        geometry.updateKeyCoordinates()
        geometry.updateHomePosition()
        return geometry
    }

}