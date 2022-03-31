package dev.aspirasoft.vread.books.data.repo

import dev.aspirasoft.vread.books.data.source.BooksDataSource
import dev.aspirasoft.vread.books.model.Book
import dev.aspirasoft.vread.core.data.Repository
import io.github.saifkhichi96.android.db.LocalDatabase
import javax.inject.Inject

/**
 * Requests Inbox data from the remote data source and maintains an in-memory
 * cache of the messages.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class BooksRepository @Inject constructor(
    dataSource: BooksDataSource,
    cache: LocalDatabase,
) : Repository<Book>(dataSource, cache) {

    fun getCategories(): List<String> {
        return this.data.map { it.category }.distinct().sorted()
    }

    suspend fun getGenres(category: String): List<String> {
        return getAll()
            .filter { it.category.equals(category, ignoreCase = true) }
            .map { it.subCategory }
            .distinct()
            .sorted()
    }

    suspend fun renameGenre(category: String, oldGenre: String, newGenre: String) {
        this.data
            .filter { it.category == category && it.subCategory == oldGenre }
            .forEach {
                it.subCategory = newGenre
                update(it.id, it)
            }
    }

    suspend fun setRead(bookId: String, readerId: String) {
        (dataSource as BooksDataSource).setReadStatus(bookId, readerId, true)
    }

    suspend fun setUnread(bookId: String, readerId: String) {
        (dataSource as BooksDataSource).setReadStatus(bookId, readerId, false)
    }

    suspend fun isRead(bookId: String, readerId: String): Boolean {
        return (dataSource as BooksDataSource).getReadStatus(bookId, readerId)
    }

}