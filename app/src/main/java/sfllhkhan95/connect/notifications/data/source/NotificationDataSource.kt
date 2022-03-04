package sfllhkhan95.connect.notifications.data.source

import com.google.android.gms.tasks.Task
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ServerValue
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import sfllhkhan95.connect.notifications.model.Notification

/**
 * A data access class to manage user notifications.
 *
 * Purpose of this class is to provide methods for communicating with the
 * database to see, update and save user notifications.
 *
 * @author saifkhichi96
 * @since 1.0.12
 */
object NotificationDataSource {

    private val db = Firebase.database.getReference("notifications/")

    /**
     * Adds a notification for multiple recipients to the database.
     *
     * @param title A short notification title or summary.
     * @param message A more detailed notification message.
     * @param recipients List of user ids of the notification recipients.
     */
    suspend fun add(title: String, message: String, recipients: List<String>): Notification? {
        recipients.forEach { recipient -> add(title, recipient, message) }
        return try {
            val recipient = recipients.firstOrNull()!!
            val ref = db.child(recipient).push()
            val key = ref.key!!
            val n = Notification(key, title, message)

            val notifications = hashMapOf<String, Any>()
            val timestamps = hashMapOf<String, Any>()
            recipients.forEach {
                notifications["${it}/${key}"] = n
                timestamps["${it}/${key}/${Notification::timestamp.name}"] = ServerValue.TIMESTAMP
            }
            db.updateChildren(notifications).await()
            db.updateChildren(timestamps).await()
            n
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * Adds a notification for a single recipient to the database.
     *
     * @param title A short notification title or summary.
     * @param message A more detailed notification message.
     * @param recipient The user id of the notification recipient.
     */
    suspend fun add(title: String, message: String, recipient: String): Notification? {
        return try {
            val ref = db.child(recipient).push()
            val n = Notification(ref.key!!, title, message)
            ref.setValue(n).await()
            ref.child(Notification::timestamp.name).setValue(ServerValue.TIMESTAMP).await()
            n
        } catch (ex: Exception) {
            null
        }
    }

    /**
     * Get all notifications of a single user.
     *
     * @param uid The user id of the notifications recipient.
     * @return The [Task] which asynchronously fetches the notification data.
     */
    fun getAll(uid: String): Task<DataSnapshot> {
        return db.child(uid).get()
    }

    /**
     * Get all unread notifications of a single user..
     *
     * @param uid The user id of the notification recipient.
     * @return The [Task] which asynchronously fetches the notification data.
     */
    fun getUnread(uid: String): Task<DataSnapshot> {
        return db.child(uid)
            .orderByChild(Notification::status.name)
            .equalTo(Notification.Status.UNREAD.toString())
            .get()
    }

    /**
     * Updates value of a notification in the database.
     *
     * @param uid The user id of the notification recipient.
     * @param notification A notification object containing new data.
     * @return The [Task] which asynchronously updates notification data.
     */
    fun update(uid: String, notification: Notification): Task<Void> {
        return db.child(uid)
            .child(notification.id)
            .setValue(notification)
    }

    /**
     * Marks a notification as read.
     *
     * @param uid The user id of the notification recipient.
     * @param notification The notification to mark as read.
     * @return The [Task] which asynchronously marks notification as read.
     */
    fun markAsRead(uid: String, notification: Notification): Task<Void> {
        return update(uid, notification.apply { status = Notification.Status.READ })
    }

    /**
     * Marks a notification as unread.
     *
     * @param uid The user id of the notification recipient.
     * @param notification The notification to mark as unread.
     * @return The [Task] which asynchronously marks notification as unread.
     */
    fun markAsUnread(uid: String, notification: Notification): Task<Void> {
        return update(uid, notification.apply { status = Notification.Status.UNREAD })
    }

}