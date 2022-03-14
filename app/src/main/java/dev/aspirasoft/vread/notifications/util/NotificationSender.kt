package dev.aspirasoft.vread.notifications.util

import android.content.Context
import com.android.volley.AuthFailureError
import com.android.volley.Response
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.notifications.data.source.NotificationDataSource
import dev.aspirasoft.vread.notifications.data.source.TokenDataSource
import dev.aspirasoft.vread.notifications.model.FirebaseMessage
import dev.aspirasoft.vread.notifications.model.Notification

/**
 * This class is responsible for sending FCM messages.
 *
 * These messages are received in [NotificationService.onMessageReceived] on the
 * target device.
 *
 * @author saifkhichi96
 * @since 1.0.12
 * @see NotificationService
 */
object NotificationSender {

    /**
     * Sends an FCM message to a user.
     *
     * @param context The application context.
     * @param title The title of the message.
     * @param message The body of the message.
     * @param recipient The user id of the recipient.
     */
    suspend fun send(context: Context, title: String, message: String, recipient: String) {
        val tokens = TokenDataSource.get(recipient)
        val notification = NotificationDataSource.add(title, message, recipient)
        notification?.let { sendAll(context, tokens, it) }
    }

    suspend fun sendAll(context: Context, title: String, message: String, recipients: List<String>) {
        recipients.forEach { recipient ->
            val tokens = TokenDataSource.get(recipient)
            val notification = NotificationDataSource.add(title, message, recipient)
            notification?.let { sendAll(context, tokens, it) }
        }
    }

    /**
     * Sends a [Notification] as an FCM message to a single device.
     *
     * An FCM message is sent to registered devices using its unique tokens. If
     * message sending fails due to an error, it is ignored.
     *
     * @param context The application context.
     * @param token The FCM token of the target devices.
     * @param notification The notification to send.
     */
    private fun send(context: Context, token: String, notification: Notification) {
        sendAll(context, listOf(token), notification)
    }

    /**
     * Sends a [Notification] as an FCM message to multiple devices.
     *
     * An FCM message is sent to a group of registered devices using their unique
     * tokens. If message sending fails due to an error, it is ignored.
     *
     * @param context The application context.
     * @param tokens A list of tokens for the target devices.
     * @param notification The notification to send.
     */
    private fun sendAll(context: Context, tokens: List<String>, notification: Notification) {
        val m = FirebaseMessage.Builder()
            .setTitle(notification.title)
            .setBody(notification.message)
            .addAllRecipients(tokens)
            .build()

        val jsonObject = m.toJson()
        val severKey = context.getString(R.string.fcm_secret_key)

        val requestQueue = Volley.newRequestQueue(context)
        requestQueue.add(object : JsonObjectRequest(Method.POST,
            "https://fcm.googleapis.com/fcm/send", jsonObject,
            Response.Listener {
                /* no-op */
            },
            Response.ErrorListener {
                // TODO: Log the error.
            }) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val params: MutableMap<String, String> = HashMap()
                params["Authorization"] = "key=$severKey"
                params["Content-Type"] = "application/json"
                return params
            }
        })
    }

}