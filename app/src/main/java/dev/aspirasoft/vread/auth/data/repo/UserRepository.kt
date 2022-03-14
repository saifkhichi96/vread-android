package dev.aspirasoft.vread.auth.data.repo

import dev.aspirasoft.vread.auth.data.source.UserDataSource
import dev.aspirasoft.vread.auth.model.User
import io.github.saifkhichi96.android.db.LocalDatabase
import javax.inject.Inject

class UserRepository @Inject constructor(
    var dataSource: UserDataSource,
    var cache: LocalDatabase
) {

    suspend fun add(user: User) {
        dataSource.add(user)
        saveInCache(user)
    }

    suspend fun getById(userId: String): Result<User> {
        val cachedUser = getFromCache(userId)
        if (cachedUser.isSuccess) return cachedUser

        val result = dataSource.getById(userId)
        result.getOrNull()?.let { remoteUser -> saveInCache(remoteUser) }
        return result
    }

    suspend fun getByEmail(email: String): Result<User> {
        cache.getOrNull<String>("user_${email.lowercase()}")?.let { userId ->
            val cachedUser = getFromCache(userId)
            if (cachedUser.isSuccess) return cachedUser
        }

        val result = dataSource.getByEmail(email)
        result.getOrNull()?.let { remoteUser -> saveInCache(remoteUser) }
        return result
    }

    suspend fun delete(userId: String) {
        dataSource.delete(userId)
        deleteFromCache(userId)
    }

    suspend fun update(user: User) {
        dataSource.update(user)
        saveInCache(user)
    }

    private fun deleteFromCache(userId: String) {
        cache.remove(userId)
        cache.getOrNull<String>("user_${userId}")?.let { email -> cache.remove("user_${email}") }
        cache.remove("user_${userId}")
    }

    private fun getFromCache(userId: String): Result<User> {
        return when (val cachedUser = cache.getOrNull<User>(userId)) {
            null -> Result.failure(NullPointerException())
            else -> Result.success(cachedUser)
        }
    }

    private fun saveInCache(user: User) {
        cache.create(user.uid, user)
        cache.create("user_${user.uid}", user.email)
        cache.create("user_${user.email.lowercase()}", user.uid)
    }

}