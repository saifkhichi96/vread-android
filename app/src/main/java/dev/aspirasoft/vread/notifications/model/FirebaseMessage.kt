package dev.aspirasoft.vread.notifications.model

import org.json.JSONArray
import org.json.JSONObject

class FirebaseMessage private constructor(
    val title: String,
    val body: String,
    private val recipients: List<String>
) {

    fun toJson(): JSONObject? {
        return try {
            val json = JSONObject()

            // Set RemoteMessage notification data
            val notification = JSONObject()
            notification.put("title", title)
            notification.put("body", body)
            json.put("notification", notification)

            // Also add same data in the RemoteMessage payload
            val data = JSONObject()
            data.put("title", title)
            data.put("text", body)
            json.put("data", data)

            // Set recipient IDs
            val registrationIds = JSONArray()
            for (item in recipients) registrationIds.put(item)
            json.put("registration_ids", registrationIds)
            json
        } catch (ex: Exception) {
            null
        }
    }

    class Builder {

        private var title: String = ""
        private var body: String = ""
        private var recipients: ArrayList<String> = ArrayList()

        fun setTitle(title: String): Builder {
            this.title = title
            return this
        }

        fun setBody(body: String): Builder {
            this.body = body
            return this
        }

        fun addRecipient(token: String): Builder {
            this.recipients.add(token)
            return this
        }

        fun addAllRecipients(tokens: List<String>): Builder {
            this.recipients.addAll(tokens)
            return this
        }

        fun build(): FirebaseMessage {
            return FirebaseMessage(title, body, recipients)
        }

    }

}