package dev.aspirasoft.vread.notifications.data.repo

import dev.aspirasoft.vread.notifications.model.NotificationList
import io.github.saifkhichi96.android.db.LocalDatabase
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