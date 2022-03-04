package sfllhkhan95.connect.notifications.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import sfllhkhan95.connect.core.model.Viewable

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