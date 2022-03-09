package com.saifkhichi.books.data.repo

import com.orhanobut.hawk.Hawk
import com.saifkhichi.books.data.source.BooksDataSource
import com.saifkhichi.books.model.Book
import javax.inject.Inject

/**
 * Requests Inbox data from the remote data source and maintains an in-memory
 * cache of the messages.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
class BooksRepository @Inject constructor(var dataSource: BooksDataSource) {

    /**
     * In-memory cache of messages
     */
    var books: ArrayList<Book>? = null
        private set

    init {
        kotlin.runCatching { books = Hawk.get(KEY) }
    }

    suspend fun listAll(): Result<List<Book>> {
        val result = dataSource.listAll()
        result.getOrNull()?.let {
            setLibraryData(it as ArrayList<Book>)
        }

        return result
    }

    fun create() = dataSource.create()

    /**
     * Updates an existing book.
     */
    suspend fun update(book: Book): Boolean {
        val oldBook = this.books?.find { it.id == book.id }
        this.books?.remove(oldBook)
        this.books?.add(book)
        return dataSource.update(book)
    }

    fun getCategories(): List<String> {
        return this.books.orEmpty()
            .map { it.category }
            .distinct()
            .sorted()
    }

    fun getGenres(category: String): List<String> {
        return this.books.orEmpty()
            .filter { it.category.equals(category, ignoreCase = true) }
            .map { it.subCategory }
            .distinct()
            .sorted()
    }

    suspend fun renameGenre(category: String, oldGenre: String, newGenre: String) {
        this.books.orEmpty()
            .filter { it.category == category && it.subCategory == oldGenre }
            .forEach {
                it.subCategory = newGenre
                update(it)
            }
    }

    suspend fun markAsRead(bookId: String, readerId: String) {
        dataSource.markAsRead(bookId, readerId)
    }

    suspend fun markAsUnread(bookId: String, readerId: String) {
        dataSource.markAsUnread(bookId, readerId)
    }

    suspend fun isRead(bookId: String, readerId: String): Boolean {
        return dataSource.isRead(bookId, readerId)
    }

    private fun setLibraryData(books: ArrayList<Book>) {
        this.books = books
        Hawk.put(KEY, books)
    }

    companion object {
        private const val KEY = "books"
    }

}