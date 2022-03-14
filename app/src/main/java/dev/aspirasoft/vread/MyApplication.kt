package dev.aspirasoft.vread

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import dev.aspirasoft.vread.notifications.util.Notifier

/**
 * @author saifkhichi96
 * @since 14/03/2018
 */

@HiltAndroidApp
class MyApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Notifier.init(this)
    }

}