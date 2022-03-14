package dev.aspirasoft.vread.chat.data.repo

import dev.aspirasoft.vread.chat.data.source.ChatDataSource
import dev.aspirasoft.vread.chat.model.Conversation
import dev.aspirasoft.vread.chat.model.Message
import io.github.saifkhichi96.android.db.model.DatabaseEvent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ChatRepository @Inject constructor(val dataSource: ChatDataSource) {

    suspend fun create(participants: ArrayList<String>, name: String? = null): Conversation {
        return dataSource.create(participants, name)
    }

    suspend fun listAll(uid: String): List<Conversation> {
        return dataSource.listAll(uid)
    }

    suspend fun observe(cid: String): Flow<DatabaseEvent<Message>> {
        return dataSource.observe(cid)
    }

    suspend fun addMessage(cid: String, message: Message) {
        dataSource.addMessage(cid, message)
    }

    suspend fun getMessages(cid: String): List<Message> {
        return dataSource.getMessages(cid)
    }

    suspend fun readMessage(cid: String, mid: String, uid: String) {
        dataSource.readMessage(cid, mid, uid)
    }

}