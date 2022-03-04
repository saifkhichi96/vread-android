package sfllhkhan95.connect.notifications.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.messaging.RemoteMessage
import sfllhkhan95.connect.R

/**
 * Responsible for showing push notifications.
 */
object Notifier {

    fun init(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create the NotificationChannel
            val name = "Connect notifications"
            val descriptionText = "Primary notification channel"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val mChannel = NotificationChannel(CHANNEL_ID, name, importance)
            mChannel.description = descriptionText
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    fun with(context: Context): NotificationCompat.Builder {
        return NotificationCompat.Builder(context, CHANNEL_ID)
    }

    /**
     * Shows an FCM message as a push notification.
     *
     * @param context The application context.
     * @param notification The FCM message to show.
     */
    fun notify(context: Context, notification: RemoteMessage.Notification) {
        val title = notification.title
        val message = notification.body
        if (title != null && message != null) {
            notify(context, title, message, notification.channelId)
        }
    }

    fun notify(context: Context, builder: NotificationCompat.Builder, reuse: Boolean = false) {
        NotificationManagerCompat.from(context)
            .notify(
                if (reuse) notificationId else ++notificationId,
                builder.build()
            )
    }

    /**
     * Shows a push notification.
     *
     * @param context The application context.
     * @param title The title of the notification.
     * @param message The detailed message of the notification.
     * @param channel (Optional) Channel id to show the notification on. This is null
     *                by default and the default channel is used.
     */
    private fun notify(context: Context, title: String, message: String, channel: String? = null) {
        val builder = with(context)
        builder.setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
        builder.setSmallIcon(R.mipmap.ic_launcher_round)

        //TODO: Implement notification click actions
        //builder.setContentIntent(PendingIntent.getActivity(
        //    context,
        //    0,
        //    Intent(context, NotificationActivity::class.java),
        //    0
        //))

        val textStyle = NotificationCompat.BigTextStyle()
        textStyle.setBigContentTitle(title)
        textStyle.bigText(message)
        builder.setStyle(textStyle)

        notify(context, builder)
    }

    private const val CHANNEL_ID = "ConnectAlerts"
    var notificationId = 0

}