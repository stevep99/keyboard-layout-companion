package io.github.colemakmods.keyboard_companion.view

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import co.touchlab.kermit.Logger
import io.github.colemakmods.keyboard_companion.platform.AndroidPlatform
import io.github.colemakmods.keyboard_companion.platform.Common

private val log = Logger.withTag("KeyboardCompanionActivity")

/**
 * Created by steve on 27/10/2014.
 */
class KeyboardCompanionActivity : ComponentActivity() {

    private val requestPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                Toast.makeText(this, "Permission grant success", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Error: Permission was NOT granted", Toast.LENGTH_SHORT).show()
            }
        }

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        log.d { "onCreate" }
        Common.platform = AndroidPlatform(this)
        setDefaultContent()
    }

    public override fun onDestroy() {
        super.onDestroy()
        log.d { "onDestroy" }
    }

    private fun setDefaultContent() {
        setContent {
            App()
        }
    }

    fun requestStoragePermission(permission: String) {
        requestPermissionLauncher.launch(permission)
    }
}