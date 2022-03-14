package dev.aspirasoft.vread.core.data

import io.github.saifkhichi96.android.db.LocalDatabase


abstract class Repository<T : Any> constructor(
    var dataSource: DataSource<T>,
    private var cache: LocalDatabase,
) {

    var data: HashMap<String, T> = getCached(dataSource.type)

    suspend fun add(item: T) {
        dataSource.add(item)
    }

    suspend fun add(itemId: String, item: T) {
        dataSource.add(itemId, item)
        cache(itemId, item)
    }

    suspend fun get(itemId: String): T? {
        val remoteItem = dataSource.get(itemId)
        remoteItem?.let { cache(itemId, it) }
        return remoteItem
    }

    open suspend fun getAll(): List<T> {
        val remoteData = dataSource.getAll()
        remoteData.forEach { item -> cache(item.toString(), item) }
        return remoteData
    }

    suspend fun delete(itemId: String) {
        dataSource.delete(itemId)
        deleteCached(itemId)
    }

    suspend fun clear() {
        dataSource.clear()
        clearCache()
    }

    private fun cache(itemId: String, item: T) {
        val cachedItems = cache.getOrDefault<ArrayList<String>>(dataSource.root, arrayListOf())
        cachedItems.add(itemId)
        cache.update(dataSource.root, cachedItems)

        cache.create("${dataSource.root}_${itemId}", item)
        data[itemId] = item
    }

    private fun getCached(itemId: String, type: Class<T>): T? {
        return kotlin.runCatching { cache.getOrThrow("${dataSource.root}_${itemId}", type) }.getOrNull()
    }

    private fun getCached(type: Class<T>): HashMap<String, T> {
        val cachedItems = HashMap<String, T>()
        val cachedItemIds = cache.getOrDefault<ArrayList<String>>(dataSource.root, arrayListOf())
        cachedItemIds.forEach { itemId ->
            getCached(itemId, type)?.let { item: T -> cachedItems[itemId] = item }
        }
        return cachedItems
    }

    private fun deleteCached(itemId: String) {
        val cachedItems = cache.getOrDefault<ArrayList<String>>(dataSource.root, arrayListOf())
        cachedItems.remove(itemId)
        cache.update(dataSource.root, cachedItems)

        cache.remove("${dataSource.root}_${itemId}")
        data.remove(itemId)
    }

    private fun clearCache() {
        val cachedItems = cache.getOrDefault<ArrayList<String>>(dataSource.root, arrayListOf())
        cachedItems.forEach { itemId -> cache.remove("${dataSource.root}_${itemId}") }
        cache.remove(dataSource.root)

        data.clear()
    }

}