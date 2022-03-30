package dev.aspirasoft.vread.core.data

import io.github.saifkhichi96.android.db.LocalDatabase


abstract class Repository<T : Any> constructor(
    var dataSource: DataSource<T>,
    private var cache: LocalDatabase,
) {

    var data: ArrayList<T> = getCached()
        private set

    fun create(): String {
        return dataSource.create()
    }

    suspend fun add(item: T) {
        dataSource.add(item)
        cache(item)
    }

    suspend fun add(itemId: String, item: T) {
        dataSource.add(itemId, item)
        cache(item)
    }

    suspend fun get(itemId: String): T? {
        val remoteItem = dataSource.get(itemId)
        remoteItem?.let { cache(it) }
        return remoteItem
    }

    open suspend fun getAll(): List<T> {
        val remoteData = dataSource.getAll()
        cache(remoteData)
        return remoteData
    }

    suspend fun update(itemId: String, item: T): Boolean {
        val updated = dataSource.update(itemId, item)
        if (updated) cache(item)
        return updated
    }

    suspend fun delete(itemId: String) {
        get(itemId)?.let { deleteCached(it) }
        dataSource.delete(itemId)
    }

    suspend fun clear() {
        dataSource.clear()
        clearCache()
    }

    private fun cache(item: T) {
        val cachedItem = try {  // Try to get the cached item
            val itemId = item::class.java.getDeclaredField("id").apply { isAccessible = true }.get(item)
            data.find { it::class.java.getDeclaredField("id").apply { isAccessible = true }.get(it) == itemId }
        } catch (ex: NoSuchFieldException) { // Item does not have an "id" field
            data.find { it == item }  // Use object equality
        } catch (ex: IllegalAccessException) {  // The "id" field is not accessible for some reason
            data.find { it == item }  // Use object equality
        }

        when (cachedItem) {
            null -> data.add(item)  // Add new item
            else -> data[data.indexOf(cachedItem)] = item  // Replace existing item
        }

        cache.update(dataSource.root, data)
    }

    private fun cache(data: List<T>) {
        this.data.clear()
        this.data.addAll(data)
        cache.update(dataSource.root, data)
    }

    private fun getCached(): ArrayList<T> {
        return cache.getOrDefault(dataSource.root, arrayListOf())
    }

    private fun deleteCached(item: T) {
        if (data.contains(item)) {
            data.remove(item)
        }

        cache.update(dataSource.root, data)
    }

    private fun clearCache() {
        cache.remove(dataSource.root)
    }

}