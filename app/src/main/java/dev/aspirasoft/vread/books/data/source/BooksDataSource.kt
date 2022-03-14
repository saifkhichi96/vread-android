package dev.aspirasoft.vread.books.data.source

import dev.aspirasoft.vread.books.model.Book
import io.github.saifkhichi96.android.db.RemoteDatabase
import javax.inject.Inject

/**
 *
 *
 * @author saifkhichi96
 */
class BooksDataSource @Inject constructor(var db: RemoteDatabase) {

    private val root = "library"

    /**
     * Adds a new book.
     */
    suspend fun add(book: Book) {
        val childKey = db.createEmptyChild(root)
        book.id = childKey

        db.createChild(root, childKey, book)
    }

    fun create() = db.createEmptyChild(root)

    suspend fun get(bookId: String): Book? {
        return db.getOrNull("$root/${bookId}")
    }

    /**
     * Updates an existing book.
     */
    suspend fun update(book: Book): Boolean {
        if (book.id.isBlank()) return false

        db.update("$root/${book.id}", book)
        return true
    }

    /**
     * Gets a list of all clients.
     */
    suspend fun listAll(): Result<List<Book>> {
        return try {
            val books = db.list(root, Book::class.java, null, null)

            Result.success(books)
        } catch (ex: Exception) {
            Result.failure(ex)
        }
    }

    suspend fun markAsRead(bookId: String, readerId: String) {
        db.update("users/${readerId}/books_read/${bookId}", true)
    }

    suspend fun markAsUnread(bookId: String, readerId: String) {
        db.remove("users/${readerId}/books_read/${bookId}")
    }

    suspend fun isRead(bookId: String, readerId: String): Boolean {
        return db.getOrNull("users/${readerId}/books_read/${bookId}") ?: false
    }

}