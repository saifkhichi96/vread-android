package com.saifkhichi.books.ui.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.color.ColorRoles
import com.google.android.material.color.MaterialColors
import com.saifkhichi.books.ui.holder.BookGenreButtonHolder
import sfllhkhan95.connect.R
import sfllhkhan95.connect.databinding.ViewCategoryBinding

class GenreAdapter(
    private val context: AppCompatActivity,
    private val dataset: List<String>,
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var onItemClicked: ((String) -> Unit)? = null

    fun setOnItemClickListener(listener: ((String) -> Unit)) {
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
        val view = ViewCategoryBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return BookGenreButtonHolder(view)
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
        val category = dataset[position].ifBlank { context.getString(R.string.book_category_none) }

        val bookGenreButtonHolder = holder as BookGenreButtonHolder
        bookGenreButtonHolder.category = category
        bookGenreButtonHolder.onItemClicked = onItemClicked

        val colors = convertToColor(holder.itemView.context, category)
        bookGenreButtonHolder.binding.root.setBackgroundColor(colors.accentContainer)
        bookGenreButtonHolder.binding.root.setTextColor(colors.onAccentContainer)
    }

    /**
     * Return the size of the item
     */
    override fun getItemCount() = dataset.size

    companion object {
        fun convertToColor(context: Context, o: Any): ColorRoles {
            val color = try {
                val i = o.hashCode()
                Color.parseColor(
                    "#FF" + Integer.toHexString(i shr 16 and 0xFF) +
                            Integer.toHexString(i shr 8 and 0xFF) +
                            Integer.toHexString(i and 0xFF)
                )
            } catch (ignored: Exception) {
                Color.parseColor("#7138D9")
            }

            val harmonizedColor = MaterialColors.harmonizeWithPrimary(context, color)
            return MaterialColors.getColorRoles(context, harmonizedColor)
        }
    }

}