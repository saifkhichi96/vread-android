package dev.aspirasoft.vread.books.ui.activity

import android.content.Intent
import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.os.LocaleList
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import androidx.appcompat.app.AppCompatActivity
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.saifkhichi.storage.CloudFileStorage
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.R
import dev.aspirasoft.vread.books.data.repo.BooksRepository
import dev.aspirasoft.vread.books.data.source.GoogleBooksAPI
import dev.aspirasoft.vread.books.model.Book
import dev.aspirasoft.vread.books.model.Book.Companion.toByteArray
import dev.aspirasoft.vread.books.model.Format
import dev.aspirasoft.vread.books.model.ISBN
import dev.aspirasoft.vread.books.ui.activity.BookDetailsActivity.Companion.EXTRA_BOOK
import dev.aspirasoft.vread.books.util.BarcodeScanner
import dev.aspirasoft.vread.databinding.ActivityEditBookBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class EditBookActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditBookBinding

    private lateinit var bookStorage: CloudFileStorage
    private lateinit var book: Book

    private var creatingBook: Boolean = false

    @Inject
    lateinit var repo: BooksRepository

    private var selectedBookCover: Uri? = null

    private lateinit var scanner: BarcodeScanner

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditBookBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_action_close)

        bookStorage = CloudFileStorage(this, "library")
        var book = intent.getSerializableExtra(EXTRA_BOOK) as Book?
        if (book == null) {
            creatingBook = true
            supportActionBar?.title = getString(R.string.title_activity_add_book)
            book = Book()
        }
        this.book = book

        // Don't show title in landscape mode
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            supportActionBar?.title = ""
        }

        // Create the barcode scanner
        scanner = BarcodeScanner(this) { barcode ->
            kotlin.runCatching {
                val isbn = ISBN(barcode!!).toString()
                onISBNScanned(isbn)
            }.onFailure { ex ->
                val error = when (ex) {
                    is IllegalArgumentException -> "Invalid ISBN"
                    is NullPointerException -> "No barcode found"
                    else -> "Failed to scan barcode"
                }
                Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
            }
        }

        updateUI()

        binding.bookCover.setOnClickListener { chooseBookCover() }

        val categories = repo.getCategories()
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        binding.bookCategory.setAdapter(categoryAdapter)

        lifecycleScope.launch {
            val genres = repo.getGenres(book.category).toMutableList()
            val genreAdapter = ArrayAdapter(this@EditBookActivity, android.R.layout.simple_list_item_1, genres)
            binding.bookSubCategory.setAdapter(genreAdapter)

            binding.bookCategory.setOnItemClickListener { _, _, position, _ ->
                lifecycleScope.launch {
                    genres.clear()
                    genres.addAll(repo.getGenres(categories[position]))
                    genreAdapter.notifyDataSetChanged()
                }
            }
        }

        val formatsAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, Format.values())
        binding.bookFormat.setAdapter(formatsAdapter)

        val languages = LocaleList.getAdjustedDefault().toLanguageTags().split(",")
        val languageAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, languages)
        binding.bookLanguage.setAdapter(languageAdapter)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK && data != null) {
            if (requestCode == RC_PICK_COVER) {
                try {
                    val uri = data.data
                    Glide.with(binding.bookCover)
                        .load(uri!!)
                        .thumbnail()
                        .placeholder(R.drawable.placeholder_book_cover)
                        .error(R.drawable.placeholder_book_cover)
                        .into(binding.bookCover)
                    selectedBookCover = uri
                } catch (ex: Exception) {
                    Snackbar.make(binding.root, ex.message ?: ex::javaClass.name, Snackbar.LENGTH_SHORT).show()
                    book.getBookCover(this@EditBookActivity, binding.bookCover)
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_edit_book, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                lifecycleScope.launch {
                    item.isEnabled = false
                    saveEdits()
                    item.isEnabled = true
                    setResult(RESULT_OK)
                }
                true
            }
            R.id.action_scan -> {
                scanner.start()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun chooseBookCover() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, getString(R.string.book_cover_select)), RC_PICK_COVER)
    }

    private suspend fun saveEdits() {
        val status = Snackbar.make(binding.root, "Saving...", Snackbar.LENGTH_INDEFINITE)
        status.show()

        if (creatingBook) book.id = repo.create()

        selectedBookCover?.let {
            try {
                status.setText("Uploading cover...")
                updateBookCover(it)
            } catch (ex: Exception) {
                status.setText(getString(R.string.book_cover_error))
            }
        }
        status.setText("Saving details...")
        book.isbn = binding.bookIsbn.text.toString()
        book.title = binding.bookTitle.text.toString()
        book.authors = binding.bookAuthors.text.toString()
        book.publishedOn = binding.bookPublishYear.text.toString().toInt()
        book.publishedBy = binding.bookPublisher.text.toString()
        book.category = binding.bookCategory.text.toString()
        book.subCategory = binding.bookSubCategory.text.toString()
        book.excerpt = binding.bookDescription.text.toString()
        book.format = Format.fromString(binding.bookFormat.text.toString())
        book.lang = binding.bookLanguage.text.toString()
        book.pageCount = binding.bookPages.text.toString().toInt()

        repo.update(book.id, book)
        setResult(RESULT_OK)
        finish()
    }

    private suspend fun updateBookCover(coverImage: Uri) {
        val bitmap = binding.bookCover.drawable.toBitmap(212, 320).toByteArray()
        val error = bookStorage.upload(book.cover(), bitmap).exceptionOrNull()
        if (error != null) throw error

        book.getBookCover(this@EditBookActivity, binding.bookCover, true)
    }

    /**
     * Called when a valid ISBN is scanned.
     *
     * If a book with the same ISBN does not already exist, the book details
     * are fetched from the Google Books API and, if found, the UI is updated.
     *
     * @param isbn The ISBN of the scanned book.
     */
    private fun onISBNScanned(isbn: String) {
        // Check if a book with the same ISBN already exists (or is being edited)
        if (creatingBook) {
            val books = repo.data
            val existing = books.find { it.isbn == isbn }
            if (existing != null) {
                showError("Book already exists")
                return
            }
        }

        // Fetch book details from Google Books API
        lifecycleScope.launch(Dispatchers.IO) {
            val volumes = GoogleBooksAPI.findByISBN(isbn) // List of books with the same ISBN
            if (volumes.isEmpty()) {
                showError("No book found with ISBN $isbn")
                return@launch
            }

            book.updateWith(volumes[0], overwrite = creatingBook)  // Use the first book in the list
            updateUI()  // Update UI with book details
        }
    }

    /**
     * Updates the UI with the book details.
     *
     * This method always runs on the UI thread.
     */
    private fun updateUI() = runOnUiThread {
        book.getBookCover(this, binding.bookCover)
        binding.bookTitle.setText(book.title)
        binding.bookAuthors.setText(book.authors)
        binding.bookIsbn.setText(book.isbn)
        binding.bookCategory.setText(book.category)
        binding.bookSubCategory.setText(book.subCategory)
        binding.bookPages.setText(book.pageCount.toString())
        binding.bookFormat.setText(book.format.toString())
        binding.bookLanguage.setText(book.lang)
        binding.bookPublisher.setText(book.publishedBy)
        binding.bookPublishYear.setText(book.publishedOn.toString())
        binding.bookDescription.setText(book.excerpt)
    }

    /**
     * Shows an error message.
     *
     * This method always runs on the UI thread.
     */
    private fun showError(error: String) = runOnUiThread {
        Snackbar.make(binding.root, error, Snackbar.LENGTH_SHORT).show()
    }

    companion object {
        const val RC_PICK_COVER = 100
    }

}