package io.github.colemakmods.keyboard_companion.platform

import androidx.compose.ui.graphics.ImageBitmap

interface Platform {
    val name: String
    fun getVersionText(): String
    fun loadExtraLayouts(): List<Pair<String, ByteArray>>?
    fun loadExtraGeometries(): List<Pair<String, ByteArray>>?
    fun saveImage(filename: String, destBitmap: ImageBitmap)
    fun outputTextKeyboard(filename: String, content: String)
    fun defaultMediaScaleFactor(): Float
}

object Common {
    lateinit var platform: Platform
}
