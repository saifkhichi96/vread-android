package com.saifkhichi.books.ui.activity

import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.transition.platform.MaterialContainerTransformSharedElementCallback
import com.saifkhichi.books.data.repo.BooksRepository
import com.saifkhichi.books.model.Book
import com.saifkhichi.books.ui.activity.BookDetailsActivity.Companion.EXTRA_BOOK
import com.saifkhichi.books.ui.activity.BookDetailsActivity.Companion.EXTRA_USER_ADMIN
import com.saifkhichi.books.ui.activity.BookDetailsActivity.Companion.EXTRA_USER_ID
import com.saifkhichi.books.ui.adapter.LibraryAdapter
import com.saifkhichi.books.ui.viewmodel.BooksViewModel
import dagger.hilt.android.AndroidEntryPoint
import sfllhkhan95.connect.R
import sfllhkhan95.connect.databinding.ActivityBooksListBinding
import javax.inject.Inject

@AndroidEntryPoint
class BooksListActivity : AppCompatActivity() {

    private lateinit var booksAdapter: LibraryAdapter
    private val library = ArrayList<LibraryAdapter.LibraryListItem<out Any>>()

    private var categoryFilter: String? = null
    private var subCategoryFilter: String? = null

    @Inject
    lateinit var repo: BooksRepository

    /**
     * View bindings for the activity.
     */
    private lateinit var binding: ActivityBooksListBinding

    /**
     * View model to observe and manipulate the activity data.
     */
    private val viewModel: BooksViewModel by viewModels()

    /**
     * Performs the refresh operation.
     */
    private val onRefreshListener = SwipeRefreshLayout.OnRefreshListener {
        viewModel.getAllBooks()
    }

    lateinit var currentUserId: String
    var currentUserAdmin: Boolean = false

    /**
     * Observes the result of the refresh operation.
     */
    private val refreshResultObserver = Observer<Result<List<Book>>> {
        binding.swipeRefresh.isRefreshing = false
        try {
            val data = it.getOrThrow()
            onRefreshed(data)
        } catch (ex: Exception) {
            onRefreshFailed(ex.message ?: ex::class.java.simpleName)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setExitSharedElementCallback(MaterialContainerTransformSharedElementCallback())
        window.sharedElementsUseOverlay = false
        super.onCreate(savedInstanceState)
        binding = ActivityBooksListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        currentUserId = intent.getStringExtra(EXTRA_USER_ID) ?: return finish()
        currentUserAdmin = intent.getBooleanExtra(BookDetailsActivity.EXTRA_USER_ADMIN, false)

        categoryFilter = intent.getStringExtra(EXTRA_CATEGORY)
        subCategoryFilter = intent.getStringExtra(EXTRA_SUBCATEGORY)
        supportActionBar?.title = subCategoryFilter ?: categoryFilter ?: getString(R.string.title_activity_library)
        if (subCategoryFilter == "Uncategorized") subCategoryFilter = ""

        booksAdapter = LibraryAdapter(this, library, grid = subCategoryFilter != null)
        booksAdapter.setOnItemClickListener { book, view -> openBookDetails(book, view) }
        booksAdapter.setOnCategoryClickListener { openLibrary(it) }

        binding.booksList.adapter = booksAdapter
        viewModel.books.observe(this, refreshResultObserver)

        binding.swipeRefresh.setOnRefreshListener(onRefreshListener)
        refreshManually()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_books_list, menu)

        // Only admins can add books
        menu.findItem(R.id.action_add).isVisible = currentUserAdmin

        // Associate searchable configuration with the SearchView
        (menu.findItem(R.id.action_search).actionView as SearchView).apply {
            this.queryHint = getString(R.string.hint_search)
            this.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return onSearchRequested(query.trim())
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    return if (newText.isEmpty()) onSearchRequested("") else false
                }
            })
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> {
                val i = Intent(this, EditBookActivity::class.java)
                startActivity(i)
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onSearchRequested(query: String): Boolean {
        booksAdapter.filter.filter(query)
        return true
    }

    private fun openBookDetails(book: Book, view: View) {
        val i = Intent(this, BookDetailsActivity::class.java)
        val options = ActivityOptions.makeSceneTransitionAnimation(
            this,
            view,
            "open_book_details" // The transition name to be matched in Activity B.
        )
        i.putExtra(EXTRA_BOOK, book)
        i.putExtra(EXTRA_USER_ID, currentUserId)
        i.putExtra(EXTRA_USER_ADMIN, currentUserAdmin)

        startActivity(i, options.toBundle())
    }

    /**
     * Opens books activity.
     */
    private fun openLibrary(subCategory: String) {
        val i = Intent(this, BooksListActivity::class.java)
        i.putExtra(EXTRA_CATEGORY, categoryFilter)
        i.putExtra(EXTRA_SUBCATEGORY, subCategory)
        i.putExtra(EXTRA_USER_ID, currentUserId)

        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

    /**
     * Refreshes the activity content manually (i.e. not with the swipe-refresh gesture)
     */
    private fun refreshManually() {
        binding.swipeRefresh.isRefreshing = true
        onRefreshListener.onRefresh()
    }

    /**
     * Called when refresh operation finishes successfully
     *
     * @param data The updated data.
     */
    private fun onRefreshed(data: List<Book>) {
        library.clear()
        val categories = data.groupBy { it.category }.toSortedMap()
        if (categoryFilter != null && subCategoryFilter != null) {
            categories[categoryFilter]
                ?.filter { it.subCategory == subCategoryFilter }
                ?.let { books ->
                    library += LibraryAdapter.LibraryBook(books)
                }
        } else {
            categories.forEach { group ->
                val category = group.key
                if (categoryFilter == null || category == categoryFilter) {
                    if (category != categoryFilter) library += LibraryAdapter.CategoryName(category)

                    val subCategories = group.value.groupBy { it.subCategory }.toSortedMap()
                    subCategories.forEach { item ->
                        val subCategory = item.key.ifBlank { getString(R.string.book_category_none) }
                        library += LibraryAdapter.SubCategoryName(
                            if (category == categoryFilter) subCategory
                            else getString(R.string.book_category).format(category, subCategory)
                        )

                        val subCategoryBooks = item.value
                            .sortedBy { it.title }
                            .sortedBy { it.publishedOn }
                            .sortedBy { it.authors }
                        library += LibraryAdapter.LibraryBook(subCategoryBooks)
                    }
                }
            }
        }
        booksAdapter.notifyDataSetChanged()
    }

    /**
     * Called when the refresh operation fails.
     *
     * @param error An error message explaining the cause of failure.
     */
    private fun onRefreshFailed(error: String) {
        Toast.makeText(this, error, Toast.LENGTH_LONG).show()
    }

    companion object {
        const val EXTRA_CATEGORY = "category"
        const val EXTRA_SUBCATEGORY = "sub_category"
    }

}