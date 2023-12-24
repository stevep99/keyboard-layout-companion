package io.github.colemakmods.keyboard_companion

import android.app.Application
import android.content.pm.PackageManager
import timber.log.Timber

class KeyboardCompanionApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

    fun getVersionText(): String {
        try {
            val pInfo = packageManager.getPackageInfo(packageName, 0)
            return "v${pInfo.versionName}"
        } catch (e: Exception) {
            Timber.e(e, "version lookup failed")
            return ""
        }
    }

}