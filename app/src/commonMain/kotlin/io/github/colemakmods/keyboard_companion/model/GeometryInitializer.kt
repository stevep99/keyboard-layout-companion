package io.github.colemakmods.keyboard_companion.model

import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.platform.Common
import keyboard_layout_companion.app.generated.resources.Res
import kotlinx.serialization.json.Json

private val log = Logger.withTag("GeometryInitializer")

private const val GEOMETRY_MANIFEST_FILE = "files/geometry/_manifest.txt"

class GeometryInitializer {
    private val json = Json {
        ignoreUnknownKeys = true
    }
    private val keyboardGeometries = ArrayList<Geometry>()

    suspend fun loadData(): List<Geometry> {
        log.d("loadData...")

        val geometryFileNames = Res.readBytes(GEOMETRY_MANIFEST_FILE)
            .decodeToString()
            .lines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .map { "files/geometry/$it".trim() }
        log.d("Geometry manifest contains ${geometryFileNames.size} files")

        geometryFileNames.forEach { resourcePath ->
            log.d("Loading geometry file $resourcePath")
            try {
                val fileBytes = Res.readBytes(resourcePath)
                val geometry = json.decodeFromString<Geometry>(fileBytes.decodeToString())
                keyboardGeometries.add(geometry)
            } catch (ex: Exception) {
                log.w("Error loading geometry resource $resourcePath", ex)
            }
        }
        Common.platform.loadExtraGeometries()?.let { extraFiles ->
            extraFiles.forEach {
                log.d("Loading extra geometry file ${it.first}")
                try {
                    val geometry = json.decodeFromString<Geometry>(it.second.decodeToString())
                    keyboardGeometries.add(geometry)
                } catch (ex: Exception) {
                    log.w("Error loading extra geometry resource ${it.first}", ex)
                }
            }
        }

        return keyboardGeometries.sorted()
    }

}