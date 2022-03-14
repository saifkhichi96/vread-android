package dev.aspirasoft.vread.feed.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Post(
    var id: String = "",
    var content: String = "",
    var attachment: String? = null,
    var postedBy: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var views: Int = 0,
) : Parcelable {

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Post

        if (id != other.id) return false

        return true
    }

    override fun hashCode() = id.hashCode()

    override fun toString() = id

}