package com.saifkhichi.books.ui.holder

import androidx.recyclerview.widget.RecyclerView
import com.saifkhichi.books.databinding.ViewBookListBinding

class BookListHolder(binding: ViewBookListBinding) : RecyclerView.ViewHolder(binding.root) {
    val bookList: RecyclerView = binding.listView
}