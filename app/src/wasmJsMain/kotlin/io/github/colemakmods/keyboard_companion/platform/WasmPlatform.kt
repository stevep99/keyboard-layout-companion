package io.github.colemakmods.keyboard_companion.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import kotlinx.browser.document
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import org.khronos.webgl.Uint8Array
import kotlin.js.toJsArray
import org.khronos.webgl.set
import org.w3c.dom.*
import org.w3c.dom.url.URL
import org.w3c.files.Blob
import org.w3c.files.BlobPropertyBag

class WasmPlatform(private val version: String): Platform {

    override val name: String
        get() = "wasm"

    override fun getVersionText(): String {
        return version
    }

    override fun formatLabel(inputLabel: String): String {
        // replace unicode chars not currently supported in Compose
        var label = inputLabel
        label = label.replace("⎇", "[A]")
        label = label.replace("⌘", "[W]")
        label = label.replace("⇧", "[S]")
        label = label.replace("⎈", "[C]")
        label = when(label) {
            "⇟" -> "PgDn"
            "⇞" -> "PgUp"
            "↑" -> "Up"
            "↓" -> "Dn"
            "←" -> "Lt"
            "→" -> "Rt"
            "⇱" -> "Home"
            "⇲" -> "End"
            "⌦" -> "Del"
            "⌫" -> "BS"
            "⇪" -> "Caps"
            "⏎" -> "Ent"
            "⇥" -> "Tab"
            "☰" -> "Mnu"
            else -> label
        }
        return label
    }

    override fun loadExtraLayouts(): List<Pair<String, ByteArray>>? {
        // not supported
        return null
    }

    override fun loadExtraGeometries(): List<Pair<String, ByteArray>>? {
        // not supported
        return null
    }

    private fun ImageBitmap.toByteArray(): ByteArray? {
        val image = Image.makeFromBitmap(this.asSkiaBitmap())
        return image.encodeToData(EncodedImageFormat.PNG)?.bytes
    }

    private fun ByteArray.toUint8ArrayAlternative(): Uint8Array {
        val uint8Array = Uint8Array(this.size) // Create Uint8Array of the correct size
        for (i in this.indices) {
            uint8Array[i] = this[i] // Set values one by one
        }
        return uint8Array
    }

    override fun saveImage(filename: String, destBitmap: ImageBitmap) {
        CoroutineScope(Dispatchers.Default).launch {
            val bytes = destBitmap.toByteArray() ?: return@launch
            val jsUint8Array = bytes.toUint8ArrayAlternative()

            val blobParts = arrayOf(jsUint8Array as JsAny?).toJsArray()

            val blob = Blob(blobParts, BlobPropertyBag(type = "image/png"))
            val url = URL.createObjectURL(blob)

            val a = document.createElement("a") as HTMLAnchorElement
            a.href = url
            a.download = filename
            document.body?.appendChild(a)
            a.click()
            document.body?.removeChild(a)

            URL.revokeObjectURL(url) // Clean up the object URL
            println("Image download initiated: $filename")
        }
    }

    override fun outputTextKeyboard(filename: String, content: String) {
        // no action
    }

}
