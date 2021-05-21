package io.github.colemakmods.keyboard_companion.view

import android.app.Application
import timber.log.Timber

class KeyboardCompanionApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }

}