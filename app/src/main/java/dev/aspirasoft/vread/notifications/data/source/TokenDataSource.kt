package dev.aspirasoft.vread.notifications.data.source

import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging
import kotlinx.coroutines.tasks.await

/**
 * TokenManager is responsible for saving and obtaining user tokens from the server.
 *
 * @author saifkhich96
 * @since 1.0.12
 * @see PushNotificationService.onNewToken
 */
object TokenDataSource {

    private val db = Firebase.database.getReference("tokens/")
    private val webService get() = Firebase.messaging

    /**
     * Get device ID from an FCM token.
     *
     * @param fcmToken The value of the FCM token.
     * @return A unique device ID.
     */
    private fun getDeviceId(fcmToken: String): String {
        return fcmToken.split(":")[0]
    }

    /**
     * Set the FCM token for the current user device.
     *
     * @param uid The id of the user.
     * @param fcmToken The value of the FCM token.
     */
    operator fun set(uid: String, fcmToken: String) {
        val deviceId = getDeviceId(fcmToken)
        db.child(uid)
            .child(deviceId)
            .setValue(fcmToken)
    }

    /**
     * Get FCM tokens for all devices of a user.
     *
     * @param uid The id of the user.
     */
    suspend fun get(uid: String): List<String> {
        val snapshot = db.child(uid).get().await()
        return snapshot?.children?.mapNotNull {
            kotlin.runCatching { it.value?.toString() }.getOrNull()
        }.orEmpty()
    }

    /**
     * Delete registered FCM token for the current user device.
     *
     * @param uid The id of the user.
     */
    suspend fun removeUserDevice(uid: String) = kotlin.runCatching{
        val fcmToken = Firebase.messaging.token.await()
        val deviceId = getDeviceId(fcmToken)
        Firebase.messaging.deleteToken()
        db.child(uid).child(deviceId).removeValue().await()
    }

    /**
     * Delete registered FCM tokens for all user devices.
     *
     * @param uid The id of the user.
     */
    suspend fun removeUserDevices(uid: String) = kotlin.runCatching {
        db.child(uid).removeValue().await()
    }

    /**
     * Requests new FCM token for current user device, and updates in database.
     *
     * @param uid The id of the user.
     */
    suspend fun update(uid: String) = kotlin.runCatching {
        val fcmToken = webService.token.await()
        this[uid] = fcmToken
    }

}