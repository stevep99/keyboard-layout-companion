package io.github.colemakmods.keyboard_companion.view

import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.webkit.WebView
import android.widget.*
import android.widget.AdapterView.OnItemSelectedListener
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.github.colemakmods.keyboard_companion.*
import io.github.colemakmods.keyboard_companion.model.Geometry
import io.github.colemakmods.keyboard_companion.model.GeometryInitializer
import io.github.colemakmods.keyboard_companion.model.Layout
import io.github.colemakmods.keyboard_companion.model.LayoutInitializer
import timber.log.Timber
import java.io.*

/**
 * Created by steve on 27/10/2014.
 */
class KeyboardCompanionActivity : Activity() {

    companion object {
        private const val DEFAULT_LAYOUT_OPTION = "Colemak-DH"
        private const val FIND_OPTIMAL_FINGER = false
        private const val SCALE_BITMAP = false
        private const val OUTPUT_BITMAP_WIDTH = 1048

        private const val PERMISSION_REQUEST_SAVE_IMAGE = 1001
        private const val PERMISSION_REQUEST_SAVE_TEXT = 1002
    }

    private val KEY_FILTER_OPTIONS = arrayOf(
            "All keys",  //0
            "All keys except bottom row",  //1
            "Character keys",  //2
            "Main zone",  //3
            "Letters & punctuation",  //4
            "Letters only" //5
    )

    private lateinit var keyboardSplit: CheckBox
    private lateinit var layerPanel: View
    private lateinit var layerName: TextView
    private lateinit var geometryList: List<Geometry>
    private lateinit var layoutList: List<Layout>
    private lateinit var currentLayout: Layout
    private lateinit var currentGeometry: Geometry
    private var currentLayer = 0
    private val options = Options()

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.keyboard)

        val gi = GeometryInitializer()
        gi.init(this, externalGeometryDir())
        geometryList = gi.getGeometryList()
        if (geometryList.isEmpty()) {
            Timber.e("Unable to initialize geometry files")
            return
        }

        Timber.d("Initialized ${geometryList.size} geometry files")
        val li = LayoutInitializer()
        li.init(this, geometryList, externalLayoutDir())
        layoutList = li.getLayouts()
        if (layoutList.isEmpty()) {
            Timber.e("Unable to initialize layout files")
            return
        }
        var initialLayoutSelection = 0
        for (i in layoutList.indices) {
            if (layoutList[i].name == DEFAULT_LAYOUT_OPTION) {
                currentLayout = layoutList[i]
                initialLayoutSelection = i
            }
        }
        currentGeometry = currentLayout.compatibleGeometries[0]
        Timber.d("Initialized ${layoutList.size} layout files")

        createModeSpinner()

        val keyboardShowStyles = findViewById<CheckBox>(R.id.keyboardShowStyles)
        keyboardShowStyles.setOnCheckedChangeListener { compoundButton, _ ->
            options.showStyles = compoundButton.isChecked
            refreshKeyboard()
        }
        options.showStyles = keyboardShowStyles.isChecked
        val keyboardShowFingers = findViewById<CheckBox>(R.id.keyboardShowFingers)
        keyboardShowFingers.setOnCheckedChangeListener { compoundButton, _ ->
            options.showFingers = compoundButton.isChecked
            refreshKeyboard()
        }
        options.showFingers = keyboardShowFingers.isChecked
        keyboardSplit = findViewById(R.id.keyboardSplit)
        keyboardSplit.setOnCheckedChangeListener { compoundButton, _ ->
            options.showSplit = compoundButton.isChecked
            refreshKeyboard()
        }
        options.showSplit = keyboardSplit.isChecked
        layerPanel = findViewById(R.id.layerPanel)
        layerName = findViewById(R.id.layerName)

        val layerPrevious = findViewById<ImageView>(R.id.layerPrevious)
        layerPrevious.setOnClickListener {
            --currentLayer
            if (currentLayer < 0) {
                currentLayer = currentLayout.layerCount - 1
            }
            refreshKeyboard()
        }

        val layerNext = findViewById<ImageView>(R.id.layerNext)
        layerNext.setOnClickListener {
            currentLayout
            ++currentLayer
            if (currentLayer >= currentLayout.layerCount) {
                currentLayer = 0
            }
            refreshKeyboard()
        }

        val keyboardLayoutSpinner = findViewById<View>(R.id.keyboardLayoutSpinner) as Spinner
        createLayoutSpinner(keyboardLayoutSpinner, initialLayoutSelection)

        createKeyFilterSpinner()
    }

    fun getVersionText(): String {
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            return "v${pInfo.versionName}"
        } catch (e: PackageManager.NameNotFoundException) {
            Timber.e(e, "version lookup failed")
            return ""
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
                showInfo()
                true
            }
            R.id.action_settings -> {
                showSettings()
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

    private fun createModeSpinner() {
        val keyboardModeSpinner = findViewById<Spinner>(R.id.modeSpinner)
        val keyDisplayAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, Options.Mode.values())
        keyDisplayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        keyboardModeSpinner.adapter = keyDisplayAdapter
        keyboardModeSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                options.mode = Options.Mode.values()[position]
                val layoutSpinnerEnabled = options.mode === Options.Mode.MODE_DISPLAY
                val keyboardLayoutSpinner = findViewById<Spinner>(R.id.keyboardLayoutSpinner)
                keyboardLayoutSpinner.isEnabled = layoutSpinnerEnabled
                createGeometrySpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun createLayoutSpinner(keyboardLayoutSpinner: Spinner, initialSelection: Int) {
        val keyboardLayoutAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, layoutList)
        keyboardLayoutAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        keyboardLayoutSpinner.adapter = keyboardLayoutAdapter
        keyboardLayoutSpinner.setSelection(initialSelection)
        keyboardLayoutSpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                currentLayout = layoutList[position]
                currentLayer = 0
                createGeometrySpinner()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun createGeometrySpinner() {
        val keyboardGeometrySpinner = findViewById<Spinner>(R.id.keyboardGeometrySpinner)
        val filteredGeometryList = if (options.mode === Options.Mode.MODE_DISPLAY) currentLayout.compatibleGeometries else geometryList
        val keyboardGeometryAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, filteredGeometryList)
        keyboardGeometryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        keyboardGeometrySpinner.adapter = keyboardGeometryAdapter
        keyboardGeometrySpinner.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                currentGeometry = keyboardGeometryAdapter.getItem(position)!!
                keyboardSplit.isEnabled = currentGeometry.split == Geometry.Split.SPLITTABLE
                if (currentGeometry.split == Geometry.Split.ALWAYS) {
                    keyboardSplit.isChecked = true
                } else if (currentGeometry.split == Geometry.Split.NEVER) {
                    keyboardSplit.isChecked = false
                }
                refreshKeyboard()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun createKeyFilterSpinner() {
        val keyboardKeyFilter = findViewById<Spinner>(R.id.keyboardKeyFilter)
        val keyFilterAdapter = ArrayAdapter(this,
                android.R.layout.simple_spinner_item, KEY_FILTER_OPTIONS)
        keyFilterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        keyboardKeyFilter.adapter = keyFilterAdapter
        keyboardKeyFilter.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View, position: Int, id: Long) {
                options.keyFilterOption = position
                refreshKeyboard()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun refreshKeyboard() {
        Timber.d("refreshKeyboard ${options.mode} ${currentLayout.name} ${currentGeometry.title}")
        val showLayerSelector: Boolean
        if (options.mode === Options.Mode.MODE_DISPLAY) {
            showLayerSelector = currentLayout.layerCount > 1
            layerPanel.visibility = if (showLayerSelector) View.VISIBLE else View.INVISIBLE
            layerName.text = currentLayout.getLayerName(currentLayer)
            currentLayout.dumpLayout(PrintWriter(System.out))
        } else {
            currentGeometry.updateDistancesScores(FIND_OPTIMAL_FINGER)
        }
        val showMultiLayers = currentLayout.isLayerMulti(currentLayer)
        var mainLayer = if (showMultiLayers) currentLayer + 1 else currentLayer

        val keyboardView = findViewById<LinearLayout>(R.id.keyboardView)
        val keyboardViewCreator = KeyboardViewCreator(this) { refreshKeyboard() }
        keyboardViewCreator.createKeyViews(keyboardView, currentGeometry, currentLayout,
                mainLayer, showMultiLayers, false, options)
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
            val filename = "${currentLayout.id}_${currentGeometry.id}.dat"
            val saveFile = File(dir, filename)

            try {
                val writer = PrintWriter(FileWriter(saveFile))
                writer.use {
                    currentGeometry.updateDistancesScores(FIND_OPTIMAL_FINGER)
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
        Timber.d("printKeyboard ${currentGeometry.title}  ${currentLayout.name}")
        val filename = if (currentLayout.layerCount > 1) {
            "${currentLayout.id}_${currentGeometry.id}_${currentLayout.getLayerName(currentLayer)}.png"
        } else {
            "${currentLayout.id}_${currentGeometry.id}.png"
        }
        val showMultiLayers = currentLayout.isLayerMulti(currentLayer)
        var mainLayer = if (showMultiLayers) currentLayer + 1 else currentLayer

        val keyboardPrintView = findViewById<LinearLayout>(R.id.keyboardPrintView)
        val keyboardViewCreator = KeyboardViewCreator(this, null)
        keyboardViewCreator.createKeyViews(keyboardPrintView, currentGeometry, currentLayout,
            mainLayer, showMultiLayers, true, options)
        keyboardPrintView.post {
            save(keyboardPrintView, filename)
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
                val strm = FileOutputStream(saveFile)
                destBitmap.compress(Bitmap.CompressFormat.PNG, 95, strm)
                strm.close()
                Timber.d("saved keyboard image to $saveFile")
                Toast.makeText(this, "Printed to $filename", Toast.LENGTH_SHORT).show()
            } catch (e: IOException) {
                Timber.w(e, "Unable to save keyboard image file")
                Toast.makeText(this, "Error: unable to save image $filename", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showInfo() {
        AlertDialog.Builder(this)
                .setView(R.layout.dlg_info)
                .setPositiveButton(android.R.string.ok) { dlg, _ -> dlg.dismiss() }
                .create()
                .also { dlg ->
                    dlg.show()
                    dlg.findViewById<TextView>(R.id.versionTextView).let {
                        it.text = getVersionText()
                    }
                    dlg.findViewById<WebView>(R.id.infoWebView).let {
                        it.settings.textZoom = 80
                        it.loadUrl("file:///android_asset/info.html")
                    }
                }
    }

    private fun showSettings() {
        AlertDialog.Builder(this)
                .setTitle(R.string.settings)
                .setView(R.layout.dlg_settings)
                .setPositiveButton(android.R.string.ok) { dlg, _ ->
                    val settingsLayout = (dlg as AlertDialog).findViewById<SettingsLayout>(R.id.settingsLayout)
                    val keyGraphicSetting = settingsLayout.getKeyGraphicSetting()
                    if (options.keyRenderOptions.javaClass != keyGraphicSetting) {
                        options.keyRenderOptions = keyGraphicSetting.newInstance()
                        refreshKeyboard()
                    }
                }
                .create()
                .also { dlg ->
                    dlg.show()
                    dlg.findViewById<SettingsLayout>(R.id.settingsLayout)
                            .putOptions(options)
                }
    }

    private fun externalOutputDir() = getExternalFilesDir("output")

    private fun externalGeometryDir() = getExternalFilesDir("geometry")

    private fun externalLayoutDir() = getExternalFilesDir("layout")

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
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