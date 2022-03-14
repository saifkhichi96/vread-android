package dev.aspirasoft.vread.notifications.util

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dev.aspirasoft.vread.notifications.data.source.TokenDataSource

/**
 * A [BroadcastReceiver] for receiving FCM registration tokens.
 *
 * @author saifkhichi96
 * @since 1.0.12
 */
class TokenReceiver(private val uid: String) : BroadcastReceiver() {

    private val dataSource = TokenDataSource

    /**
     * Receives the broadcast.
     *
     * If broadcast contains an FCM token, the token is saved on to the server.
     *
     * @see NotificationService.onNewToken
     */
    override fun onReceive(context: Context, intent: Intent) {
        when (intent.action) {
            NotificationService.REGISTRATION_SUCCESS -> {
                val fcmToken = intent.getStringExtra(NotificationService.NEW_TOKEN)
                if (fcmToken !== null) {
                    dataSource[uid] = fcmToken
                }
            }
            NotificationService.REGISTRATION_ERROR -> {
                // TODO: Log the error.
            }
        }
    }

    suspend fun requestNewToken() {
        dataSource.update(uid)
    }

    fun register(context: Context) {
        val manager = LocalBroadcastManager.getInstance(context)
        manager.registerReceiver(this, IntentFilter(NotificationService.REGISTRATION_SUCCESS))
        manager.registerReceiver(this, IntentFilter(NotificationService.REGISTRATION_ERROR))
    }

    fun unregister(context: Context) {
        val manager = LocalBroadcastManager.getInstance(context)
        manager.unregisterReceiver(this)
    }
}