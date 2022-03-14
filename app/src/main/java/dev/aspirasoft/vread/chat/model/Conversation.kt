package dev.aspirasoft.vread.chat.model

import dev.aspirasoft.vread.core.model.Viewable

/**
 * Conversation is a chatroom where a group of participants can communicate with
 * each other by sending and receiving [Message]s.
 *
 * @author saifkhichi96
 * @version 1.0.0
 */
data class Conversation(
    var id: String = "",
    var name: String? = null,
    var participants: ArrayList<String> = arrayListOf(),
) : Viewable() {

//    val history: List<TextMessage>
//        get() {
//            val history: ArrayList<TextMessage> = ArrayList()
//            messages.let {
//                for (message in it) {
//                    message.body.let { messageBody ->
//                        if (message.senderId == auth.currentUser?.uid) {
//                            history.add(TextMessage.createForLocalUser(messageBody, message.timestamp))
//                        } else {
//                            message.senderId.let { messageSender ->
//                                history.add(TextMessage.createForRemoteUser(messageBody,
//                                    message.timestamp,
//                                    messageSender))
//                            }
//                        }
//
//                    }
//                }
//            }
//            return history.sortedWith(compareBy { it.zzb() })
//        }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        if (!super.equals(other)) return false

        other as Conversation

        if (id != other.id) return false

        return true
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

}