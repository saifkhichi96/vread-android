package dev.aspirasoft.vread.books.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import dev.aspirasoft.vread.books.data.repo.BooksRepository
import dev.aspirasoft.vread.books.ui.activity.BookDetailsActivity
import dev.aspirasoft.vread.books.ui.activity.BooksListActivity
import dev.aspirasoft.vread.books.ui.adapter.GenreAdapter
import dev.aspirasoft.vread.databinding.ActivityLibraryBinding
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class LibraryFragment : Fragment() {

    lateinit var binding: ActivityLibraryBinding

    @Inject
    lateinit var repo: BooksRepository

    private lateinit var currentUserId: String
    private var currentUserAdmin: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = ActivityLibraryBinding.inflate(inflater, container, false)

        currentUserId = arguments?.getString("user") ?: ""
        currentUserAdmin = arguments?.getBoolean("edit_mode") ?: false
        updateUI()

        return binding.root
    }

    private fun updateUI() {
        lifecycleScope.launch {
            val fictionGenres = repo.getGenres("Fiction")
            val fictionAdapter = GenreAdapter(requireContext(), fictionGenres)
            binding.fictionCategories.adapter = fictionAdapter
            fictionAdapter.setOnItemClickListener { openLibrary("Fiction", it) }

            val nonFictionGenres = repo.getGenres("Non-Fiction")
            val nonFictionAdapter = GenreAdapter(requireContext(), nonFictionGenres)
            binding.nonFictionCategories.adapter = nonFictionAdapter
            nonFictionAdapter.setOnItemClickListener { openLibrary("Non-Fiction", it) }
        }
    }

    /**
     * Opens books activity.
     */
    private fun openLibrary(category: String, subCategory: String) {
        val i = Intent(requireContext(), BooksListActivity::class.java)
        i.putExtra(BooksListActivity.EXTRA_CATEGORY, category)
        i.putExtra(BooksListActivity.EXTRA_SUBCATEGORY, subCategory)
        i.putExtra(BookDetailsActivity.EXTRA_USER_ID, currentUserId)
        i.putExtra(BookDetailsActivity.EXTRA_USER_ADMIN, currentUserAdmin)

        startActivity(i)
    }

    companion object {
        fun newInstance(uid: String, editMode: Boolean): LibraryFragment {
            val args = Bundle()
            args.putString("user", uid)
            args.putBoolean("edit_mode", editMode)

            val fragment = LibraryFragment()
            fragment.arguments = args
            return fragment
        }
    }

}