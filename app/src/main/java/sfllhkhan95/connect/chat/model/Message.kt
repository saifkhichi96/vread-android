package sfllhkhan95.connect.chat.model

import sfllhkhan95.connect.core.model.Viewable
import java.text.DateFormat
import java.util.*


/**
 * ChatMessage is a message sent in a chatroom.
 *
 * A message is sent by one user to either another individual user, or to a group of users
 * who are all members of a same group chat (e.g. in same class, subject, school, etc).
 *
 * @param body The body of the message.
 * @param senderId The [User.id] of the message sender.
 * @param timestamp The timestamp when the message was sent.
 * @param readReports A ([User.id], Timestamp) HashMap for all recipients who have read the message.
 */
data class Message constructor(
    var id: String = "",
    var body: String = "",
    var senderId: String = "",
    var timestamp: Long = System.currentTimeMillis(),
    var readReports: HashMap<String, Long> = HashMap<String, Long>().apply {
        put(senderId, System.currentTimeMillis())
    },
) : Viewable() {

    fun timeSentOn() = DateFormat
        .getTimeInstance(DateFormat.SHORT, Locale.getDefault())
        .format(timestamp)
        .toString()

    fun dateSentOn() = DateFormat
        .getDateInstance(DateFormat.SHORT, Locale.getDefault())
        .format(timestamp)
        .toString()

    /**
     * Checks if the message was read by a user.
     *
     * @param uid The id of the user to check.
     * @return True if user has read the message, else false.
     */
    fun isReadBy(uid: String) = readReports.containsKey(uid)

    /**
     * Checks when the message was read by a user.
     *
     * @param uid The id of the user to check.
     * @return Timestamp when the user read this message, or null if message not read by the user.
     */
    fun whenReadBy(uid: String) = readReports[uid]

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Message

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }


}