package dev.aspirasoft.vread.books.data.source

import dev.aspirasoft.vread.books.model.Book
import dev.aspirasoft.vread.core.data.DataSource
import io.github.saifkhichi96.android.db.RemoteDatabase
import javax.inject.Inject

/**
 * Data source for books.
 *
 * This class is responsible for fetching and storing user books from the
 * remote database. It provides methods for creating, updating, deleting and
 * retrieving books.
 *
 * @author AspiraSoft
 * @since 0.0.1
 *
 * @constructor Creates a new data source for books.
 * @param db The remote database to use for data storage.
 */
class BooksDataSource @Inject constructor(db: RemoteDatabase) : DataSource<Book>(db, Book::class.java) {

    override val root = "library"

    /**
     * Set read status of a book for a user.
     *
     * @param bookId ID of the book to update.
     * @param readerId The user ID of the reader.
     * @param read Whether the book has been read or not.
     */
    suspend fun setReadStatus(bookId: String, readerId: String, read: Boolean) {
        when {
            read -> db.update("users/${readerId}/books_read/${bookId}", true)
            else -> db.remove("users/${readerId}/books_read/${bookId}")
        }
    }

    /**
     * Get read status of a book for a user.
     *
     * @param bookId ID of the book to update.
     * @param readerId The user ID of the reader.
     * @return Whether the book has been read or not.
     */
    suspend fun getReadStatus(bookId: String, readerId: String): Boolean {
        return db.getOrNull("users/${readerId}/books_read/${bookId}") ?: false
    }

}