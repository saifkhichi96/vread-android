package dev.aspirasoft.vread.auth.data.source

import dev.aspirasoft.vread.auth.model.User
import io.github.saifkhichi96.android.db.RemoteDatabase
import java.util.*
import javax.inject.Inject


/**
 * Defines a remote data source for app users.
 *
 * Purpose of this class is to provide an API for communicating with the
 * remote database to access and modify data related to users.
 *
 * @author saifkhichi96
 *
 * @property root The location of users data on the remote database.
 */
class UserDataSource @Inject constructor(var db: RemoteDatabase) {

    private val root = "users"

    /**
     * Adds a new [User] to the remote database.
     *
     * @param user The user to add.
     */
    suspend fun add(user: User) {
        db.create("$root/${user.uid}/meta/", user)
    }

    /**
     * Gets a user from the remote database.
     *
     * @param userId The id of the user to get.
     */
    suspend fun getById(userId: String): Result<User> {
        return kotlin.runCatching {
            val user = db.getOrThrow(
                "$root/$userId/meta/",
                User::class.java
            )
            Result.success(user)
        }.getOrElse { Result.failure(it) }
    }

    /**
     * Gets user data by user's email address.
     *
     * @param email The email address of the user account to get.
     */
    suspend fun getByEmail(email: String): Result<User> {
        return try {
            Result.success(
                db.list(
                    root,
                    User::class.java,
                    "meta/${User::email.name}",
                    email.lowercase(Locale.getDefault())
                ).firstOrNull()!!
            )
        } catch (ex: Exception) {
            return Result.failure(ex)
        }
    }

    /**
     * Deletes user data from database.
     *
     * @param userId The id of the user to delete.
     */
    suspend fun delete(userId: String) {
        db.remove("$root/$userId/")
    }

    suspend fun update(user: User) {
        db.update("$root/${user.uid}/meta/", user)
    }

}