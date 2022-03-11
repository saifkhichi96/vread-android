package com.saifkhichi.books.ui.holder

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.saifkhichi.books.model.Book
import sfllhkhan95.connect.databinding.ViewBookBinding

class BookHolder(binding: ViewBookBinding) : RecyclerView.ViewHolder(binding.root) {
    var book: Book? = null

    val bookCover: ImageView = binding.bookCover
    val bookTitle: TextView = binding.title
    val bookAuthors: TextView = binding.authors

    var onItemClicked: ((thread: Book, View) -> Unit)? = null

    init {
        binding.root.setOnClickListener {
            book?.let { onItemClicked?.invoke(it, itemView) }
        }
    }
}