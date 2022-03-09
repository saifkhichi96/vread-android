package com.saifkhichi.books.ui.activity

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
import com.saifkhichi.books.R
import com.saifkhichi.books.data.repo.BooksRepository
import com.saifkhichi.books.data.source.GoogleBooksAPI
import com.saifkhichi.books.databinding.ActivityEditBookBinding
import com.saifkhichi.books.model.Book
import com.saifkhichi.books.model.Book.Companion.toByteArray
import com.saifkhichi.books.model.Format
import com.saifkhichi.books.ui.activity.BookDetailsActivity.Companion.EXTRA_BOOK
import com.saifkhichi.storage.CloudFileStorage
import dagger.hilt.android.AndroidEntryPoint
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

        updateUI()

        binding.bookCover.setOnClickListener { chooseBookCover() }

        val categories = repo.getCategories()
        val categoryAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, categories)
        binding.bookCategory.setAdapter(categoryAdapter)

        val genres = repo.getGenres(book.category).toMutableList()
        val genreAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, genres)
        binding.bookSubCategory.setAdapter(genreAdapter)

        binding.bookCategory.setOnItemClickListener { _, _, position, _ ->
            genres.clear()
            genres.addAll(repo.getGenres(categories[position]))
            genreAdapter.notifyDataSetChanged()
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
                val isbn13 = binding.bookIsbn.text?.toString() ?: ""
                if (isbn13.length == 13) {
                    Thread {
                        GoogleBooksAPI.findByISBN(isbn13).firstOrNull()?.let {
                            runOnUiThread {
                                book.updateWith(it)
                                updateUI()
                            }
                        }
                    }.start()
                }
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

        repo.update(book)
        setResult(RESULT_OK)
        finish()
    }

    private suspend fun updateBookCover(coverImage: Uri) {
        val bitmap = binding.bookCover.drawable.toBitmap(212, 320).toByteArray()
        val error = bookStorage.upload(book.cover(), bitmap).exceptionOrNull()
        if (error != null) throw error

        book.getBookCover(this@EditBookActivity, binding.bookCover, true)
    }

    private fun updateUI() {
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

    companion object {
        const val RC_PICK_COVER = 100
    }

}