package dev.aspirasoft.vread.books.ui.activity

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.transition.platform.MaterialContainerTransform
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.google.zxing.BarcodeFormat
import com.saifkhichi.storage.CloudFileStorage
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.books.data.repo.BooksRepository
import dev.aspirasoft.vread.books.data.source.GoogleBooksAPI
import dev.aspirasoft.vread.books.model.Book
import dev.aspirasoft.vread.books.util.BarcodeEncoder
import dev.aspirasoft.vread.databinding.ActivityBookDetailsBinding
import kotlinx.coroutines.launch
import javax.inject.Inject


@AndroidEntryPoint
class BookDetailsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBookDetailsBinding

    private lateinit var bookStorage: CloudFileStorage
    private lateinit var book: Book
    private var bookIsRead = false

    private lateinit var readButton: MenuItem
    private lateinit var unreadButton: MenuItem

    private lateinit var currentUserId: String
    private var currentUserAdmin: Boolean = false

    @Inject
    lateinit var repo: BooksRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        findViewById<View>(android.R.id.content).transitionName = "open_book_details"
        setEnterSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementEnterTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 300L
        }
        window.sharedElementReturnTransition = MaterialContainerTransform().apply {
            addTarget(android.R.id.content)
            duration = 250L
        }
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = ""
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_close)

        bookStorage = CloudFileStorage(this, "library")
        book = intent.getSerializableExtra(EXTRA_BOOK) as Book? ?: return finish()
        currentUserId = intent.getStringExtra(EXTRA_USER_ID) ?: return finish()
        currentUserAdmin = intent.getBooleanExtra(EXTRA_USER_ADMIN, false)
        updateUI()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            RC_EDIT_BOOK -> if (resultCode == RESULT_OK) {
                lifecycleScope.launch {
                    book = repo.get(book.id) ?: book
                    updateUI()
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_book_details, menu)
        readButton = menu.findItem(R.id.action_read)
        unreadButton = menu.findItem(R.id.action_unread)

        // Only admins can edit books
        menu.findItem(R.id.action_edit).isVisible = currentUserAdmin
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                editBook()
                true
            }
            R.id.action_read -> {
                markAsRead()
                true
            }
            R.id.action_unread -> {
                markAsUnread()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun editBook() {
        val i = Intent(this, EditBookActivity::class.java)
        i.putExtra(EXTRA_BOOK, book)

        startActivityForResult(i, RC_EDIT_BOOK)
    }

    private fun setReadStatus(read: Boolean) {
        bookIsRead = read
        // fixme: show read status
//        if (read) {
//            readButton.isVisible = false
//            unreadButton.isVisible = true
//        } else {
//            readButton.isVisible = true
//            unreadButton.isVisible = false
//        }
    }

    private fun markAsRead() {
        Snackbar.make(binding.root, "Do you really want to mark this book as read?", Snackbar.LENGTH_LONG)
            .setAction(android.R.string.ok) {
                lifecycleScope.launch {
                    repo.setRead(book.id, currentUserId)
                    setReadStatus(true)
                }
            }.show()
    }

    private fun markAsUnread() {
        Snackbar.make(binding.root, "Do you really want to mark this book as unread?", Snackbar.LENGTH_LONG)
            .setAction(android.R.string.ok) {
                lifecycleScope.launch {
                    repo.setUnread(book.id, currentUserId)
                    setReadStatus(false)
                }
            }.show()
    }

    private fun updateUI() {
        lifecycleScope.launch {
            setReadStatus(repo.isRead(book.id, currentUserId))
        }

        book.getBookCover(this, binding.bookCover)
        binding.bookTitle.text = book.title
        binding.bookAuthors.text = book.authors

        binding.bookCategory.text = getString(R.string.book_category).format(
            book.category, book.subCategory.ifBlank { getString(R.string.book_category_none) }
        )

        binding.bookPages.text = book.pageCount.takeIf { it > 0 }?.toString() ?: getString(R.string.value_none)
        binding.bookFormat.text = book.format.toString().ifBlank { getString(R.string.value_none) }
        binding.bookLanguage.text = book.lang.ifBlank { getString(R.string.value_none) }

        binding.bookPublisher.text = getString(R.string.book_publisher).format(
            book.publishedBy,
            if (book.publishedOn > 0) getString(R.string.book_publish_year).format(book.publishedOn) else ""
        )

        binding.bookIsbn.text = book.isbn
        binding.bookDescription.text = book.excerpt.ifBlank { getString(R.string.book_description_none) }

        // if isbn is available
        val isbn = book.isbn13()
        if (isbn.isNotBlank()) {
            try {
                val barcodeEncoder = BarcodeEncoder()

                val qrColor = TypedValue()
                theme.resolveAttribute(R.attr.colorOnSurface, qrColor, true)
                barcodeEncoder.setForegroundColor(qrColor.data)
                barcodeEncoder.setBackgroundColor(Color.TRANSPARENT)
                val bitmap = barcodeEncoder.encodeBitmap(book.isbn13(), BarcodeFormat.EAN_13, 512, 128)
                binding.bookBarcode.setImageBitmap(bitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }

            Thread {
                GoogleBooksAPI.findByISBN(isbn).firstOrNull()?.let { book ->
                    runOnUiThread {
                        binding.previewButton.visibility = View.VISIBLE
                        binding.previewButton.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(
                                book.volumeInfo.canonicalVolumeLink
                                    .replace(".html?hl=&id=", "/")
                                    .replace("books/about/", "books/edition/")
                                    .replace("books.google", "google")
                            )
                            startActivity(i)
                        }
                    }
                }
            }.start()

        }
    }

    companion object {
        const val EXTRA_BOOK = "book"
        const val EXTRA_USER_ID = "user_id"
        const val EXTRA_USER_ADMIN = "user_admin"
        const val RC_EDIT_BOOK = 100
    }

}