package dev.aspirasoft.vread.books.ui.holder

import androidx.recyclerview.widget.RecyclerView
import dev.aspirasoft.vread.databinding.ViewBookListBinding

class BookListHolder(binding: ViewBookListBinding) : RecyclerView.ViewHolder(binding.root) {
    val bookList: RecyclerView = binding.listView
}