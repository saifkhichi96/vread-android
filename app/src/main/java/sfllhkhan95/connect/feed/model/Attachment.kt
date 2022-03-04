package sfllhkhan95.connect.feed.model

import android.net.Uri

data class Attachment(
    var name: String,
    var data: Uri,
) {
    val type get() = name.substringAfterLast(".")
}