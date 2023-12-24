package io.github.colemakmods.keyboard_companion.view

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.FrameLayout
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.key
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.colemakmods.keyboard_companion.R
import io.github.colemakmods.keyboard_companion.view.dialog.InfoDialog
import io.github.colemakmods.keyboard_companion.view.dialog.SettingsDialog
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.FileWriter
import java.io.IOException
import java.io.PrintWriter


/**
 * Created by steve on 27/10/2014.
 */
class KeyboardCompanionActivity : ComponentActivity() {

    companion object {
        private const val SCALE_BITMAP = false
        private const val OUTPUT_BITMAP_WIDTH = 1048

        private const val PERMISSION_REQUEST_SAVE_IMAGE = 1001
        private const val PERMISSION_REQUEST_SAVE_TEXT = 1002
    }

    private val viewModel by viewModels<MainViewModel>()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDefaultContent()
    }

    private fun setDefaultContent() {
        setContent {
            MaterialTheme {
                InfoDialog(viewModel)
                SettingsDialog(viewModel)
                Column {
                    ControlsPanel(viewModel)
                    key(viewModel.drawRefreshState) {
                        KeyboardPanel(viewModel)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_info -> {
                viewModel.showInfoDialog = true
                true
            }
            R.id.action_settings -> {
                viewModel.showSettingsDialog = true
                true
            }
            R.id.action_save -> {
                printKeyboard()
                true
            }
            R.id.action_output_text -> {
                outputTextKeyboard()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun outputTextKeyboard() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_SAVE_TEXT)
        } else {
            outputTextKeyboardActual()
        }
    }

    private fun outputTextKeyboardActual() {
        externalOutputDir()?.let { dir ->
            val currentLayout = viewModel.currentLayout ?: return
            val currentGeometry = viewModel.currentGeometry ?: return
            val filename = "${currentLayout.id}_${currentGeometry.id}.dat"
            val saveFile = File(dir, filename)

            try {
                val writer = PrintWriter(FileWriter(saveFile))
                writer.use {
                    currentGeometry.updateDistancesScores()
                    currentLayout.dumpAll(it, currentGeometry)
                }
                Timber.d("Saved keyboard text file to $saveFile")
                Toast.makeText(this, "Written to $filename", Toast.LENGTH_SHORT).show()
            } catch(e: Exception) {
                Timber.w(e, "Unable to write text file to $filename")
                Toast.makeText(this, "Error: unable to write to $filename", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun printKeyboard() {
        if (ContextCompat.checkSelfPermission(this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_SAVE_IMAGE)
        } else {
            printKeyboardActual()
        }
    }

    private fun printKeyboardActual() {
        val rootView = FrameLayout(this)
        val keyboardPrintView = ComposeView(this)
        keyboardPrintView.apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                MaterialTheme {
                    KeyboardPanel(viewModel)
                }
            }
            rootView.addView(this)
        }
        val progressView = ComposeView(this)
        progressView.apply {
            setContent {
                MaterialTheme {
                    ProgressPanel()
                }
            }
            rootView.addView(this)
        }
        setContentView(rootView)

        keyboardPrintView.postDelayed( {
            save(keyboardPrintView, getOutputImageFilename())
            setDefaultContent()

        }, 500L)
    }

    private fun getOutputImageFilename(): String {
        val currentLayout = viewModel.currentLayout ?: return "?"
        val currentGeometry = viewModel.currentGeometry ?: return "?"
        val split = if (viewModel.options.showSplit) "_split" else ""
        return if (currentLayout.layerCount > 1) {
            val layerPart = currentLayout.getLayerName(viewModel.currentLayer).replace("\\s+".toRegex(), "_")
                .lowercase()
            "${currentLayout.id}_${currentGeometry.id}${split}_$layerPart.png"
        } else {
            "${currentLayout.id}_${currentGeometry.id}$split.png"
        }
    }

    private fun save(view: View, filename: String) {
        view.isDrawingCacheEnabled = true
        val srcBitmap = view.drawingCache
        val destBitmap = if (SCALE_BITMAP) {
            val scale = OUTPUT_BITMAP_WIDTH.toFloat() / srcBitmap.width
            Timber.d("saving keyboard at scale $scale")
            Bitmap.createScaledBitmap(srcBitmap,
                    (srcBitmap.width * scale).toInt(),
                    (srcBitmap.height * scale).toInt(),
                    true)
        } else {
            srcBitmap
        }
        externalOutputDir()?.let { dir ->
            val saveFile = File(dir, filename)
            try {
                FileOutputStream(saveFile).use {
                    destBitmap.compress(Bitmap.CompressFormat.PNG, 95, it)
                }
                Timber.d("Saved keyboard image to $saveFile")
                Toast.makeText(this, "Printed to $filename", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Timber.w(e, "Unable to save keyboard image file")
                Toast.makeText(this, "Error: unable to save image $filename", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun externalOutputDir() = getExternalFilesDir("output")

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_SAVE_IMAGE -> {
                if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    printKeyboardActual()
                }
            }
            PERMISSION_REQUEST_SAVE_TEXT -> {
                if (permissions[0] == Manifest.permission.WRITE_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    outputTextKeyboardActual()
                }
            }
        }
    }

}