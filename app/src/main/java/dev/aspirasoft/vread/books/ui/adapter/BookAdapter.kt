package dev.aspirasoft.vread.books.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import dev.aspirasoft.vread.books.model.Book
import dev.aspirasoft.vread.books.ui.holder.BookHolder
import dev.aspirasoft.vread.databinding.ViewBookBinding
import kotlin.math.roundToInt

class BookAdapter(
    private val context: FragmentActivity,
    private val dataset: List<Pair<Book, Boolean>>,
    private val itemWidth: Int,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClicked: ((Book, View) -> Unit)? = null

    fun setOnItemClickListener(listener: ((Book, View) -> Unit)) {
        onItemClicked = listener
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
        val view = ViewBookBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookHolder(view)
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
        val book = dataset[position].first
        val isRead = dataset[position].second

        val bookHolder = holder as BookHolder

        if (itemWidth > 0) {
            val params = bookHolder.bookCover.layoutParams
            params.width = itemWidth
            params.height = (160 / 112.0 * itemWidth).roundToInt()
            bookHolder.bookCover.layoutParams = params
        }
        book.getBookCover(context, bookHolder.bookCover)

        bookHolder.bookTitle.text = book.title
        bookHolder.bookAuthors.text = book.authors

        if (isRead) {
            bookHolder.bookTitle.setTextColor(context.resources.getColor(android.R.color.holo_green_dark))
        } else {
            bookHolder.bookTitle.setTextColor(context.resources.getColor(android.R.color.holo_red_dark))
        }

        bookHolder.book = book
        bookHolder.onItemClicked = onItemClicked
    }

    /**
     * Return the size of the item
     */
    override fun getItemCount() = dataset.size

}