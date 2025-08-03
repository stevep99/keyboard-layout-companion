package io.github.colemakmods.keyboard_companion.platform

import android.graphics.Bitmap
import android.widget.Toast
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.view.KeyboardCompanionActivity
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter

private val log = Logger.withTag("Platform")

class AndroidPlatform(private val activity: KeyboardCompanionActivity): Platform {

    private val outputDir = activity.getExternalFilesDir("output")

    override val name: String
        get() = "android"

    override fun getVersionText(): String {
        try {
            val pInfo = activity.packageManager.getPackageInfo(activity.packageName, 0)
            return pInfo.versionName ?: ""
        } catch (e: Exception) {
            log.e( "version lookup failed", e)
            return ""
        }
    }

    override fun formatLabel(inputLabel: String): String = inputLabel

    override fun loadExtraLayouts(): List<Pair<String, ByteArray>>? {
        val layoutDir = activity.getExternalFilesDir("layout")
        return layoutDir?.list()
            ?.filter { it.endsWith(".keyb") }
            ?.map { fileName ->
                log.d { "Loading layout file $fileName" }
                Pair(fileName, File(layoutDir, fileName).inputStream().readBytes())
            }
    }

    override fun loadExtraGeometries(): List<Pair<String, ByteArray>>? {
        val layoutDir = activity.getExternalFilesDir("geometry")
        return layoutDir?.list()
            ?.filter { it.endsWith(".json") }
            ?.map { fileName ->
                log.d { "Loading geometry file $fileName" }
                Pair(fileName, File(layoutDir, fileName).inputStream().readBytes())
            }
    }

    override fun saveImage(filename: String, destBitmap: ImageBitmap) {
//        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            activity.requestStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            return
//        }

        if (outputDir == null) {
            Toast.makeText(activity, "Unable to get output directory", Toast.LENGTH_SHORT)
                .show()
            return
        }
        val saveFile = File(outputDir, filename)
        try {
            FileOutputStream(saveFile).use {
                destBitmap.asAndroidBitmap().compress(Bitmap.CompressFormat.PNG, 95, it)
            }
            log.d("Saved keyboard image to $saveFile")
            Toast.makeText(activity, "Image saved to $filename", Toast.LENGTH_SHORT).show()
        } catch (e: IOException) {
            log.w("Unable to save keyboard image file", e)
            Toast.makeText(activity, "Error: unable to save image $filename", Toast.LENGTH_SHORT)
                .show()
        }
    }

    override fun outputTextKeyboard(filename: String, content: String) {
//        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
//            activity.requestStoragePermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//            return
//        }

        val saveFile = File(outputDir, filename)
        try {
            val writer = PrintWriter(FileWriter(saveFile))
            writer.use {
                it.write(content)
            }
            log.d("Saved keyboard text file to $saveFile")
            Toast.makeText(activity, "Written to $filename", Toast.LENGTH_SHORT).show()
        } catch(e: Exception) {
            log.w("Unable to write text file to $filename", e)
            Toast.makeText(activity, "Error: unable to write to $filename", Toast.LENGTH_SHORT).show()
        }
    }

}
