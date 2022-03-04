package sfllhkhan95.connect.chat.data.source

import com.google.firebase.database.ServerValue
import io.github.saifkhichi96.android.db.RemoteDatabase
import io.github.saifkhichi96.android.db.model.DatabaseEvent
import kotlinx.coroutines.flow.Flow
import sfllhkhan95.connect.chat.model.Conversation
import sfllhkhan95.connect.chat.model.Message
import javax.inject.Inject

class ChatDataSource @Inject constructor(val db: RemoteDatabase) {

    private fun userChats(uid: String) = "users/${uid}/chats/"

    private val root = "chat"
    private fun conversation(id: String) = "${root}/${id}"
    private fun messages(cid: String) = "${conversation(cid)}/messages"

    /**
     * Create a new conversation.
     *
     * @param participants List of ids of users participating in this conversation.
     * @param name (Optional) Name of the conversation. Only use for group chats.
     */
    suspend fun create(participants: ArrayList<String>, name: String? = null): Conversation {
        val conversationId = db.createEmptyChild(root)
        val conversation = Conversation(conversationId, name, participants)
        db.createChild(root, conversationId, conversation)
        participants.forEach { uid -> db.createChild(userChats(uid), conversationId) }
        return conversation
    }

    /**
     * Get list of all conversations of a user.
     *
     * @param uid The id of the user.
     * @return A [Flow] which emits each [Conversation] as it is received.
     */
    suspend fun listAll(uid: String): List<Conversation> {
        return db
            .list(userChats(uid), String::class.java, null, null)
            .mapNotNull { db.getOrNull(conversation(it)) }
    }

    /**
     * Observe messages in a conversation.
     *
     * @param cid The id of the conversation.
     */
    suspend fun observe(cid: String): Flow<DatabaseEvent<Message>> {
        return db.observeChildren(messages(cid), Message::class.java)
    }

    /**
     * Add a message to the chatroom.
     *
     * @param cid The id of the conversation.
     * @param message The message to add.
     */
    suspend fun addMessage(cid: String, message: Message) {
        val path = messages(cid)
        val childKey = db.createEmptyChild(path)
        message.id = childKey
        db.createChild(path, childKey, message)
    }

    /**
     * Get all messages in a conversation.
     *
     * @param cid The id of the conversation.
     */
    suspend fun getMessages(cid: String): List<Message> {
        return db.list(messages(cid), Message::class.java, null, null)
    }

    /**
     * Mark a message in a conversation as read by a user.
     *
     * @param cid The id of the conversation.
     * @param mid The id of the message.
     * @param uid The id of the user.
     */
    suspend fun readMessage(cid: String, mid: String, uid: String) {
        val path = "${messages(cid)}/${mid}/${Message::readReports.name}/${uid}"
        db.update(path, ServerValue.TIMESTAMP)
    }

}