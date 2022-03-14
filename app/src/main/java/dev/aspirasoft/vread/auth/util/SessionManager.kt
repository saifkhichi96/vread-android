package dev.aspirasoft.vread.auth.util

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import dev.aspirasoft.vread.auth.data.repo.UserRepository
import dev.aspirasoft.vread.auth.model.Credential
import dev.aspirasoft.vread.auth.model.Session
import dev.aspirasoft.vread.auth.model.User
import io.github.saifkhichi96.android.db.LocalDatabase
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Manages authentication sessions and enables read and write operations on session data.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class SessionManager @Inject constructor(var cache: LocalDatabase, var repo: UserRepository) {

    /**
     * The Firebase authentication backend.
     */
    private val auth: FirebaseAuth = Firebase.auth

    /**
     * The active [Session].
     */
    var currentSession: Session?
        get() {
            val session = cache.getOrNull<Session>(SAVED_SESSION)

            // Ensure that the session data stored in local database belongs to the
            // user that is currently signed in with the Firebase backend. If not,
            // then there was some error and we need to invalidate all session data.
            if (auth.currentUser?.uid != session?.user?.uid) {
                finish()
                return null
            }

            return session
        }
        set(value) {
            when (value) {
                null -> cache.remove(SAVED_SESSION)
                else -> cache.create(SAVED_SESSION, value)
            }
        }

    /**
     * The currently signed in [User].
     */
    val currentUser: User? get() = currentSession?.user

    /**
     * Starts a new session.
     *
     * A new user is signed out with the given [credential], and upon successful
     * authentication, user details are stored as session data.
     *
     * If another session is already active, that is finished before starting
     * the new session.
     *
     * @param credential The credentials to sign the user with.
     * @return The signed in [User].
     * @throws Exception Thrown if authentication fails.
     */
    @Throws(Exception::class)
    suspend fun start(credential: Credential): User {
        this.finish()

        try {
            val result = auth.signInWithEmailAndPassword(credential.username, credential.password).await()
            val user = repo.getById(result.user!!.uid).getOrThrow()
            saveSessionData(user)
            return user
        } catch (ex: Exception) {
            finish()
            throw ex
        }
    }

    fun saveSessionData(user: User) {
        currentSession = Session(user)
    }

    /**
     * Ends the current session.
     *
     * This includes deleting all session data, and signing out the user.
     */
    fun finish() {
        auth.signOut()
        currentSession = null
    }

    companion object {
        private const val SAVED_SESSION = "app_currentSession"
    }

}