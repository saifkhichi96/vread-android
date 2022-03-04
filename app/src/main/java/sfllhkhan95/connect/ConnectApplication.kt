package sfllhkhan95.connect

import android.app.Application
import dagger.hilt.android.HiltAndroidApp
import sfllhkhan95.connect.notifications.util.Notifier

/**
 * @author saifkhichi96
 * @since 14/03/2018
 */

@HiltAndroidApp
class ConnectApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        Notifier.init(this)
    }

}