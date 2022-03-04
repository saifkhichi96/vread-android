package sfllhkhan95.connect.notifications.data.repo

import io.github.saifkhichi96.android.db.LocalDatabase
import sfllhkhan95.connect.notifications.model.NotificationList
import javax.inject.Inject

class NotificationRepository @Inject constructor(val db: LocalDatabase) {

    var notifications = db.getOrNull<NotificationList>(SAVED_NOTIFICATIONS)

    fun saveAll(list: NotificationList) {
        when (notifications) {
            null -> {
                db.create(SAVED_NOTIFICATIONS, list)
                notifications = list
            }
            else -> {
                notifications!!.addAll(list)
                db.update(SAVED_NOTIFICATIONS, notifications!!)
            }
        }
    }

    companion object {
        private const val SAVED_NOTIFICATIONS = "app_notifications"
    }

}