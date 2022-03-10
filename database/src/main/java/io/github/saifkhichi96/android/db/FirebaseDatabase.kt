package io.github.saifkhichi96.android.db

import android.content.Context
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import io.github.saifkhichi96.android.db.model.DatabaseEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

/**
 * Implements the [RemoteDatabase] with Firebase Realtime Database.
 *
 * @constructor Creates a new instance of [FirebaseDatabase].
 * @param context The application context.
 * @param persistent Whether the database should be persistent. If true, a local cache will be used to
 *                   speed up the database operations. If false, persistence will be disabled. If null,
 *                   no changes to the default persistence behavior of Firebase will be made. Defaults to
 *                   null.
 *
 * @author saifkhichi96
 * @since 1.0.0
 * @see RemoteDatabase
 */
class FirebaseDatabase @Inject constructor(val context: Context, persistent: Boolean? = null) : RemoteDatabase() {

    private val db = Firebase.database

    init {
        persistent?.let { db.setPersistenceEnabled(it) }
    }

    @ExperimentalCoroutinesApi
    override suspend fun <T : Any> observe(path: String, type: Class<T>): Flow<T> =
        callbackFlow {
            val valueEventListener = object : ValueEventListener {
                override fun onDataChange(s: DataSnapshot) {
                    kotlin.runCatching { s.getValue(type)?.let(this@callbackFlow::trySend) }
                }

                override fun onCancelled(error: DatabaseError) {
                    close(error.toException())
                }
            }

            db.getReference(path).apply {
                addValueEventListener(valueEventListener)
                awaitClose { removeEventListener(valueEventListener) }
            }
        }

    @ExperimentalCoroutinesApi
    override suspend fun <T : Any> observeChildren(path: String, type: Class<T>): Flow<DatabaseEvent<T>> =
        callbackFlow {
            val childEventListener = object : ChildEventListener {
                override fun onChildAdded(s: DataSnapshot, p: String?) {
                    kotlin.runCatching { s.getValue(type)?.let { trySend(DatabaseEvent.Created(it)) } }
                }

                override fun onChildChanged(s: DataSnapshot, p: String?) {
                    kotlin.runCatching { s.getValue(type)?.let { trySend(DatabaseEvent.Changed(it)) } }
                }

                override fun onChildRemoved(s: DataSnapshot) {
                    kotlin.runCatching { s.getValue(type)?.let { trySend(DatabaseEvent.Deleted(it)) } }
                }

                override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) {}

                override fun onCancelled(error: DatabaseError) {
                    trySend(DatabaseEvent.Cancelled(error))
                }
            }

            db.getReference(path).apply {
                addChildEventListener(childEventListener)
                awaitClose { removeEventListener(childEventListener) }
            }
        }

    override suspend fun <T : Any> list(path: String, type: Class<T>, filter: String?, equalTo: String?): List<T> {
        return kotlin.runCatching {
            val list = ArrayList<T>()
            val ref = Firebase.database.getReference(path)
            when {
                filter != null && equalTo != null -> ref.orderByChild(filter).equalTo(equalTo)
                else -> ref
            }.get().await().children.forEach { snapshot ->
                snapshot.getValue(type)?.let { item ->
                    list.add(item)
                }
            }
            list
        }.getOrThrow()
    }

    override suspend fun clear() {
        db.reference.removeValue().await()
    }

    override suspend fun create(path: String, value: Any) {
        val ref = db.getReference(path)
        val timestamp = "timestamp"
        ref.setValue(value).await()
        if (value::class.members.any { it.name.equals(timestamp, true) }) {
            ref.child(timestamp).setValue(ServerValue.TIMESTAMP).await()
        }
    }

    override fun createEmptyChild(path: String): String {
        return db.getReference(path).push().key!!
    }

    override suspend fun <T : Any> getOrThrow(path: String, type: Class<T>): T {
        return db.getReference(path).get().await().getValue(type) ?: throw NoSuchElementException()
    }

    override suspend fun update(path: String, value: Any) {
        kotlin.runCatching { db.getReference(path).setValue(value).await() }
    }

    override suspend fun remove(path: String) {
        kotlin.runCatching { db.getReference(path).removeValue().await() }
    }

}