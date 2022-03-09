package com.saifkhichi.books.ui.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.saifkhichi.books.data.repo.BooksRepository
import com.saifkhichi.books.databinding.ActivityLibraryBinding
import com.saifkhichi.books.ui.adapter.GenreAdapter
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LibraryActivity : AppCompatActivity() {

    lateinit var binding: ActivityLibraryBinding

    @Inject
    lateinit var repo: BooksRepository

    private lateinit var currentUserId: String
    private var currentUserAdmin: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        currentUserId = intent.getStringExtra(BookDetailsActivity.EXTRA_USER_ID) ?: return finish()
        currentUserAdmin = intent.getBooleanExtra(BookDetailsActivity.EXTRA_USER_ADMIN, false)
        updateUI()
    }

    private fun updateUI() {
        val fictionGenres = repo.getGenres("Fiction")
        val fictionAdapter = GenreAdapter(this, fictionGenres)
        binding.fictionCategories.adapter = fictionAdapter
        fictionAdapter.setOnItemClickListener { openLibrary("Fiction", it) }

        val nonFictionGenres = repo.getGenres("Non-Fiction")
        val nonFictionAdapter = GenreAdapter(this, nonFictionGenres)
        binding.nonFictionCategories.adapter = nonFictionAdapter
        nonFictionAdapter.setOnItemClickListener { openLibrary("Non-Fiction", it) }
    }

    /**
     * Opens books activity.
     */
    private fun openLibrary(category: String, subCategory: String) {
        val i = Intent(this, BooksListActivity::class.java)
        i.putExtra(BooksListActivity.EXTRA_CATEGORY, category)
        i.putExtra(BooksListActivity.EXTRA_SUBCATEGORY, subCategory)
        i.putExtra(BookDetailsActivity.EXTRA_USER_ID, currentUserId)
        i.putExtra(BookDetailsActivity.EXTRA_USER_ADMIN, currentUserAdmin)

        startActivity(i)
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
    }

}