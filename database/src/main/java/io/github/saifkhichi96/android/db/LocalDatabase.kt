package io.github.saifkhichi96.android.db

/**
 * Defines an interface for managing on-device app data.
 *
 * Purpose of this class is to provide on-device storage for caching data
 * received from [RemoteDatabase], and to read and write any other local
 * app data.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
abstract class LocalDatabase {

    /**
     * Deletes all data from database.
     */
    abstract fun clear()

    /**
     * Saves an object at a path in the database.
     *
     * If the path is not empty, existing data is overwritten.
     *
     * @param path A path in the database.
     * @param value The object to save.
     */
    abstract fun create(path: String, value: Any)

    /**
     * Gets data stored at a path.
     *
     * @param path The path of the data.
     * @param type The class type of the data.
     * @return The data stored at the given path.
     * @throws Exception When no data is stored at given path.
     */
    @Throws(Exception::class)
    abstract fun <T : Any> getOrThrow(path: String, type: Class<T>): T

    /**
     * Gets data stored at a path.
     *
     * @param path The path of the data.
     * @param defValue The default value to return if nothing saved at path.
     * @return The data stored at given path, or the default value.
     */
    inline fun <reified T : Any> getOrDefault(path: String, defValue: T): T {
        return getOrNull(path) ?: defValue
    }

    /**
     * Gets data stored at a path.
     *
     * @param path The path of the data.
     * @return The data stored at given path, or null.
     */
    inline fun <reified T : Any> getOrNull(path: String): T? {
        return kotlin.runCatching { getOrThrow(path, T::class.java) }.getOrNull()
    }

    /**
     * Gets data stored at a path.
     *
     * @param path The path of the data.
     * @param onFailure The callback to execute if nothing saved at path.
     * @return The data stored at given path, or result of [onFailure] callback.
     */
    inline fun <reified T : Any> getOrElse(path: String, onFailure: (exception: Throwable) -> T): T {
        return try {
            getOrThrow(path, T::class.java)
        } catch (ex: Exception) {
            onFailure(ex)
        }
    }

    /**
     * Deletes data stored at a path.
     *
     * @param path The path of the data.
     */
    abstract fun remove(path: String)

    /**
     * Updates existing data at a path.
     *
     * If no data existed at given path, new record is created.
     *
     * @param path The path of the data.
     * @param value The updated data to save.
     */
    abstract fun update(path: String, value: Any)

}