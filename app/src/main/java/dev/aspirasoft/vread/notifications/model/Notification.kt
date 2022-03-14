package dev.aspirasoft.vread.notifications.model

import android.os.Parcelable
import dev.aspirasoft.vread.core.model.Viewable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Notification(
    var id: String = "",
    var title: String = "",
    var message: String = "",
    var status: Status = Status.UNREAD,
    var timestamp: Long = System.currentTimeMillis(),
) : Viewable(), Parcelable {

    enum class Status {
        UNREAD,
        READ
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Notification

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}