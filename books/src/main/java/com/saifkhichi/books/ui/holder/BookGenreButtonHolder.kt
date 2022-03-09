package com.saifkhichi.books.ui.holder

import androidx.recyclerview.widget.RecyclerView
import com.saifkhichi.books.databinding.ViewCategoryBinding

class BookGenreButtonHolder(val binding: ViewCategoryBinding) : RecyclerView.ViewHolder(binding.root) {

    var category: String? = null
        set(value) {
            field = value
            binding.root.text = value
        }

    var onItemClicked: ((category: String) -> Unit)? = null

    init {
        binding.root.setOnClickListener {
            category?.let { onItemClicked?.invoke(it) }
        }
    }
}