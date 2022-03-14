package dev.aspirasoft.vread.auth.util

import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.GenericTypeIndicator
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import dev.aspirasoft.vread.auth.data.repo.UserRepository
import dev.aspirasoft.vread.auth.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * AccountManager is a business-layer class which manages user accounts.
 *
 * @author saifkhichi96
 * @version 1.0
 */
class AccountManager @Inject constructor(val db: UserRepository) {

    private val auth = Firebase.auth

    /**
     * Creates a new user account.
     */
    suspend fun createUserAccount(user: User, password: String): User {
        var isUsernameUnique = true
        val type = object : GenericTypeIndicator<HashMap<String, HashMap<String, Any?>>>() {}
        Firebase.database.getReference("users/").get().await().getValue(type)?.values?.let {
            for (m in it.iterator()) {
                val username = (m["meta"] as HashMap<String, Any?>?)?.get("username")
                if (username != null && username.toString().equals(user.username, true)) {
                    isUsernameUnique = false
                    break
                }
            }
        }
        if (!isUsernameUnique) throw IllegalStateException()

        auth.createUserWithEmailAndPassword(user.email, password)
            .await()

        try {
            user.uid = auth.currentUser!!.uid
            db.add(user)
            return user
        } catch (ex: Exception) {
            deleteUserAccount(user.email, password)
            throw ex
        }
    }

    /**
     * Email a password reset link to the user.
     *
     * A password reset link is emailed to the given address. This happens asynchronously.
     * When the operation is complete, a callback is triggered.
     *
     * The operation may fail if the email is invalid, or no user with such email
     * address exists.
     *
     * @param [email] user's email address
     */
    suspend fun sendPasswordResetEmail(email: String) {
        auth.sendPasswordResetEmail(email).await()
    }

    /**
     * Deletes a user account.
     *
     * @param [email] email of the user account to delete
     * @param [password] password of the user account to delete
     */
    suspend fun deleteUserAccount(email: String, password: String) {
        auth.currentUser?.let { user ->
            val credential = EmailAuthProvider.getCredential(email, password)
            kotlin.runCatching { user.reauthenticate(credential).await() }
                .getOrElse { throw NullPointerException("Password is incorrect.") }
            kotlin.runCatching { user.delete().await() }
                .getOrElse { throw NullPointerException("Failed to delete account. Please try again.") }
            db.delete(user.uid)
        }
    }

    suspend fun updateUserAccount(user: User) {
        db.update(user)
    }

}