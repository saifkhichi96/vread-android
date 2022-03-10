package io.github.saifkhichi96.android.db

import android.content.Context
import com.orhanobut.hawk.Hawk
import javax.inject.Inject

/**
 * Implements the [LocalDatabase] with Shared Preferences.
 *
 * @constructor Creates a new instance of the [PrefsDatabase] class.
 * @param context The application context.
 *
 * @author saifkhichi96
 * @since 1.0.0
 * @see LocalDatabase
 */
class PrefsDatabase @Inject constructor(context: Context) : LocalDatabase() {

    init {
        if (!Hawk.isBuilt()) Hawk.init(context).build()
    }

    override fun clear() {
        Hawk.deleteAll()
    }

    override fun create(path: String, value: Any) {
        Hawk.put(path, value)
    }

    override fun <T : Any> getOrThrow(path: String, type: Class<T>): T {
        return try {
            Hawk.get(path)!!
        } catch (ex: Exception) {
            if (ex is NullPointerException) throw NoSuchElementException() else throw ex
        }
    }

    override fun remove(path: String) {
        Hawk.delete(path)
    }

    override fun update(path: String, value: Any) {
        Hawk.put(path, value)
    }

}