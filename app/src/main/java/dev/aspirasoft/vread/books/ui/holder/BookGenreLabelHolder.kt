package dev.aspirasoft.vread.books.ui.holder

import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import dev.aspirasoft.vread.databinding.ViewBookCategoryBinding

class BookGenreLabelHolder(binding: ViewBookCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

    var category: String? = null
    val bookCategory: TextView = binding.bookCategory
    var onItemClicked: ((category: String) -> Unit)? = null

    init {
        binding.moreButton.setOnClickListener {
            category?.let { onItemClicked?.invoke(it) }
        }
    }

}