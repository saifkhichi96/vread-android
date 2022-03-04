package sfllhkhan95.connect.notifications.util

import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * An instance of [FirebaseMessagingService] for receiving push notifications.
 *
 * Notifications are received from Firebase Cloud Messaging (FCM). New device tokens
 * for FCM are also received here.
 *
 * @author saifkhichi96
 * @since 1.0.12
 */
class NotificationService : FirebaseMessagingService() {

    var title: String? = null
    var message: String? = null

    /**
     * Called when a new FCM token is generated.
     *
     * A broadcast is sent with the new token, so that it can be handled
     * by an appropriate receiver.
     *
     * @param token The new FCM token.
     */
    override fun onNewToken(token: String) {
        // Create an intent for storing the token in to our server
        val broadcast: Intent = try {
            val intent = Intent(REGISTRATION_SUCCESS)
            intent.putExtra(NEW_TOKEN, token)
            intent
        } catch (ex: Exception) {
            Intent(REGISTRATION_ERROR)
        }

        // Send the broadcast that registration is completed
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcast)
    }

    /**
     * Called when a message is received.
     *
     * The incoming message is used to create and show a push notification.
     *
     * @param remoteMessage The FCM message.
     */
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        val notification = remoteMessage.notification
        if (notification != null) {
            Notifier.notify(applicationContext, notification)
        }
    }

    companion object {
        const val REGISTRATION_SUCCESS = "RegistrationSuccess"
        const val REGISTRATION_ERROR = "RegistrationError"
        const val NEW_TOKEN = "token"
    }

}