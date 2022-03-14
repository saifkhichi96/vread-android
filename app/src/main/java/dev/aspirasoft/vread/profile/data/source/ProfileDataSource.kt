package dev.aspirasoft.vread.profile.data.source

import com.google.firebase.database.FirebaseDatabase
import dev.aspirasoft.vread.auth.data.repo.UserRepository
import dev.aspirasoft.vread.auth.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 *
 *
 * @author saifkhichi96
 * @version 1.0.0
 * @since 1.0.0 2019-05-16 17:18
 */
class ProfileDataSource @Inject constructor(val repo: UserRepository) {

    suspend fun getFollowers(uid: String): List<User> {
        return repo.dataSource.db
            .getOrNull<HashMap<String, String>>("users/$uid/followers/")?.values.orEmpty()
            .mapNotNull { followerId -> repo.getById(followerId).getOrNull() }
    }

    suspend fun getFollowerCount(uid: String): Int {
        return repo.dataSource.db
            .getOrNull<HashMap<String, String>>("users/$uid/followers/")?.size ?: 0
    }

    suspend fun getFollowing(uid: String): List<User> {
        return repo.dataSource.db
            .getOrNull<HashMap<String, String>>("users/$uid/following/")?.values.orEmpty()
            .mapNotNull { followerId -> repo.getById(followerId).getOrNull() }
    }

    suspend fun getFollowingCount(uid: String): Int {
        return repo.dataSource.db
            .getOrNull<HashMap<String, String>>("users/$uid/following/")?.size ?: 0
    }

    suspend fun getFriends(uid: String): List<User> {
        val followers = getFollowers(uid)
        val following = getFollowing(uid)
        return followers.intersect(following).toList()
    }

    suspend fun addFollower(uid: String, followerId: String) {
        repo.dataSource.db.createChild("users/$uid/followers/", followerId)
        repo.dataSource.db.createChild("users/$followerId/following/", uid)
    }

    suspend fun removeFollower(uid: String, followerId: String) {
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid/followers/")
        ref.orderByValue()
            .equalTo(followerId)
            .get()
            .await()
            .key?.let { ref.child(it).removeValue() }

        val ref2 = FirebaseDatabase.getInstance().getReference("users/$followerId/following/")
        ref2.orderByValue()
            .equalTo(uid)
            .get()
            .await()
            .key?.let { ref2.child(it).removeValue() }
    }

}