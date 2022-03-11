package com.saifkhichi.books.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.saifkhichi.books.model.Book
import com.saifkhichi.books.ui.activity.BooksListActivity
import com.saifkhichi.books.ui.holder.BookGenreLabelHolder
import com.saifkhichi.books.ui.holder.BookListHolder
import kotlinx.coroutines.launch
import sfllhkhan95.connect.R
import sfllhkhan95.connect.databinding.ViewBookCategoryBinding
import sfllhkhan95.connect.databinding.ViewBookListBinding
import kotlin.math.roundToInt


class LibraryAdapter(
    private val context: BooksListActivity,
    private val dataset: ArrayList<LibraryListItem<out Any>>,
    private val grid: Boolean = false,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), Filterable {

    private var filteredDataset = ArrayList<LibraryListItem<out Any>>()

    init {
        filteredDataset = dataset
    }

    private var onItemClicked: ((Book, View) -> Unit)? = null
    private var onCategoryClicked: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Book, View) -> Unit)) {
        onItemClicked = listener
    }

    fun setOnCategoryClickListener(listener: (String) -> Unit) {
        onCategoryClicked = listener
    }

    /**
     * Create new views, which defines the UI of the list item
     *
     * @param parent The ViewGroup into which the new View will be added after it is bound to an adapter position.
     * @param viewType The view type of the new View.
     *
     * @return A new ViewHolder that holds a View of the given view type.
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            TYPE_CATEGORY -> {
                val view = ViewBookCategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BookGenreLabelHolder(view)
            }
            TYPE_SUB_CATEGORY -> {
                val view = ViewBookCategoryBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BookGenreLabelHolder(view)
            }
            else -> {
                val view = ViewBookListBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
                BookListHolder(view)
            }
        }
    }

    /**
     * Replace the contents of a view (invoked by the layout manager)
     *
     * Get element from the dataset at this [position] and replace the
     * contents of the view with that element
     *
     * @param holder The view to update with new content
     * @param position The position of new content in the dataset
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (val item = filteredDataset[position]) {
            is LibraryBook -> {
                if (holder is BookListHolder) {
                    val colWidthCalc = when {
                        grid -> {
                            val metrics = context.resources.displayMetrics
                            val padding = context.resources.getDimension(R.dimen.book_spacing)
                            val screenWidth = metrics.widthPixels - 2 * padding
                            val colWidthPref = context.resources.getDimension(R.dimen.book_width) + 2 * padding
                            val columnsCount = (screenWidth / colWidthPref).roundToInt()
                            holder.bookList.layoutManager = GridLayoutManager(context, columnsCount)

                            (screenWidth / columnsCount - 2 * padding).roundToInt()
                        }
                        else -> {
                            holder.bookList.layoutManager =
                                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                            0
                        }
                    }
                    context.lifecycleScope.launch {
                        val adapter = BookAdapter(
                            context,
                            item.value
                                .sortedBy { it.title }
                                .sortedBy { it.publishedOn }
                                .sortedBy { it.authors }
                                .map { Pair(it, context.repo.isRead(it.id, context.currentUserId)) },
                            colWidthCalc
                        )
                        adapter.setOnItemClickListener { book, view -> onItemClicked?.invoke(book, view) }
                        holder.bookList.adapter = adapter
                        adapter.notifyDataSetChanged()
                    }
                }
            }
            is CategoryName -> {
                if (holder is BookGenreLabelHolder) {
                    val category = item.value
                    holder.category = category
                    holder.bookCategory.text = category
                    holder.onItemClicked = onCategoryClicked
                }
            }
            is SubCategoryName -> {
                if (holder is BookGenreLabelHolder) {
                    val subCategory = item.value
                    holder.category = subCategory
                    holder.bookCategory.text = subCategory
                    holder.onItemClicked = onCategoryClicked
                }
            }
        }
    }

    /**
     * Return the size of the item
     */
    override fun getItemCount() = filteredDataset.size

    /**
     * Return the type of the item
     */
    override fun getItemViewType(position: Int) = filteredDataset[position].type

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence): FilterResults {
                val results = FilterResults()
                if (constraint.isNotEmpty()) {
                    val filteredDataset = ArrayList<LibraryListItem<out Any>>()
                    var lastCategory: SubCategoryName? = null
                    for (item in dataset) {
                        if (item is SubCategoryName) lastCategory = item
                        else if (item is LibraryBook) {
                            val books = item.value
                            val filteredBooks = books.filter {
                                it.isbn.equals(constraint.toString(), true) ||
                                        it.isbn13().equals(constraint.toString(), true) ||
                                        it.title.contains(constraint, true) ||
                                        it.authors.contains(constraint, true)
                            }

                            if (filteredBooks.isNotEmpty()) {
                                lastCategory?.let { filteredDataset.add(it) }

                                val filteredItem = LibraryBook(filteredBooks)
                                filteredDataset.add(filteredItem)
                            }
                        }
                    }
                    results.count = filteredDataset.size
                    results.values = filteredDataset
                } else {
                    results.count = dataset.size
                    results.values = dataset
                }
                return results
            }

            override fun publishResults(constraint: CharSequence, results: FilterResults) {
                filteredDataset = results.values as ArrayList<LibraryListItem<out Any>>
                notifyDataSetChanged()
            }
        }
    }

    sealed class LibraryListItem<T : Any>(val value: T, val type: Int)
    class LibraryBook(books: List<Book>) : LibraryListItem<List<Book>>(books, TYPE_BOOKS)
    class CategoryName(title: String) : LibraryListItem<String>(title, TYPE_CATEGORY)
    class SubCategoryName(title: String) : LibraryListItem<String>(title, TYPE_SUB_CATEGORY)

    companion object {
        private const val TYPE_BOOKS = 0
        private const val TYPE_CATEGORY = 1
        private const val TYPE_SUB_CATEGORY = 2
    }

}