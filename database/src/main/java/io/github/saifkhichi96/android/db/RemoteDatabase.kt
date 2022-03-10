package io.github.saifkhichi96.android.db

import io.github.saifkhichi96.android.db.model.DatabaseEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow

/**
 * Defines an interface for managing a remote database.
 *
 * Purpose of this class is to provide a two-way communication interface
 * with a remote, cloud-based database so that the app can read and write
 * app data from the cloud.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
abstract class RemoteDatabase {

    /**
     * Deletes all data from database.
     */
    abstract suspend fun clear()

    /**
     * Saves an object at a path in the database.
     *
     * If the path is not empty, existing data is overwritten.
     *
     * @param path A path in the database.
     * @param value The object to save.
     */
    abstract suspend fun create(path: String, value: Any)

    /**
     * Adds a placeholder for a child node at a path.
     *
     * @param path The path where to add the new child node.
     * @return A unique key for the new child node.
     */
    abstract fun createEmptyChild(path: String): String

    /**
     * Saves an object at a new child node of a path in the database.
     *
     * A timestamp-based unique key is assigned to the child node.
     *
     * @param path A path in the database.
     * @param value The object to save as a new child.
     */
    suspend fun createChild(path: String, value: Any) {
        val childKey = createEmptyChild(path)
        createChild(path, childKey, value)
    }

    /**
     * Saves an object at a child node of a path in the database.
     *
     * If there is an existing child key at this path, its data is overwritten.
     *
     * @param path A path in the database.
     * @param childKey The key of the child node.
     * @param value The object to save at the child node.
     */
    suspend fun createChild(path: String, childKey: String, value: Any) {
        create("${path}/${childKey}", value)
    }

    /**
     * Gets data stored at a path.
     *
     * @param path The path of the data.
     * @param type The class type of the data.
     * @return The data stored at the given path.
     * @throws Exception When no data is stored at given path.
     */
    @Throws(Exception::class)
    abstract suspend fun <T : Any> getOrThrow(path: String, type: Class<T>): T

    /**
     * Gets data stored at a path.
     *
     * @param path The path of the data.
     * @param defValue The default value to return if nothing saved at path.
     * @return The data stored at given path, or the default value.
     */
    suspend inline fun <reified T : Any> getOrDefault(path: String, defValue: T): T {
        return getOrNull(path) ?: defValue
    }

    /**
     * Gets data stored at a path.
     *
     * @param path The path of the data.
     * @return The data stored at given path, or null.
     */
    suspend inline fun <reified T : Any> getOrNull(path: String): T? {
        return kotlin.runCatching { getOrThrow(path, T::class.java) }.getOrNull()
    }

    /**
     * Gets data stored at a path.
     *
     * @param path The path of the data.
     * @param onFailure The callback to execute if nothing saved at path.
     * @return The data stored at given path, or result of [onFailure] callback.
     */
    suspend inline fun <reified T : Any> getOrElse(path: String, onFailure: (exception: Throwable) -> T): T {
        return try {
            getOrThrow(path, T::class.java)
        } catch (ex: Exception) {
            onFailure(ex)
        }
    }

    /**
     * Gets list of objects stored at a path.
     *
     * @param path The path of the data list.
     * @param type The type of list objects.
     * @param filter (Optional) Name of attribute to filter the objects by. Set [null] for no filtering.
     * @param equalTo (Optional) Value of the filter attribute to match. Set [null] for no filtering.
     */
    abstract suspend fun <T : Any> list(path: String, type: Class<T>, filter: String?, equalTo: String?): List<T>

    /**
     * Observes data stored at a path for any changes.
     *
     * A new object is emitted everytime there is a change.
     *
     * @param path The path of the data to observe.
     * @param type The class type of the data.
     * @return A [Flow] of the objects, which emits a new object on every update.
     */
    @ExperimentalCoroutinesApi
    abstract suspend fun <T : Any> observe(path: String, type: Class<T>): Flow<T>

    /**
     * Observes data stored at child nodes of a path for any changes.
     *
     * A new [DatabaseEvent] is emitted everytime there is a change, including
     * creation of a new child, changing existing child, and removal of a child.
     *
     * @param path The path of the data to observe.
     * @param type The class type of the children.
     * @return A [Flow] of [DatabaseEvent]s, which emits a child object on every update.
     */
    @ExperimentalCoroutinesApi
    abstract suspend fun <T : Any> observeChildren(path: String, type: Class<T>): Flow<DatabaseEvent<T>>

    /**
     * Deletes data stored at a path.
     *
     * @param path The path of the data.
     */
    abstract suspend fun remove(path: String)

    /**
     * Updates existing data at a path.
     *
     * If no data existed at given path, new record is created.
     *
     * @param path The path of the data.
     * @param value The updated data to save.
     */
    abstract suspend fun update(path: String, value: Any)

}