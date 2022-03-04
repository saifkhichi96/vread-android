package sfllhkhan95.connect.core.data

import io.github.saifkhichi96.android.db.RemoteDatabase


abstract class DataSource<T : Any> constructor(var db: RemoteDatabase, var type: Class<T>) {

    abstract val root: String

    suspend fun add(item: T) = add(db.createEmptyChild(root), item)

    suspend fun add(itemId: String, item: T) {
        db.createChild(root, itemId, item)
        db.createChild("$root/$itemId", "id", itemId)
    }

    suspend fun get(itemId: String): T? = kotlin.runCatching {
        db.getOrThrow("$root/$itemId", type)
    }.getOrNull()

    suspend fun getAll(): List<T> {
        return db.list(root, type, null, null)
    }

    suspend fun delete(itemId: String) = db.remove("$root/$itemId")

    suspend fun clear() = db.remove(root)

}