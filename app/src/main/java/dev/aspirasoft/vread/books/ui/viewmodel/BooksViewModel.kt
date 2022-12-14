package dev.aspirasoft.vread.books.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dev.aspirasoft.vread.books.data.repo.BooksRepository
import dev.aspirasoft.vread.books.model.Book
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Stores and prepares the Inbox data for displaying.
 *
 * @author saifkhichi96
 * @since 1.0.0
 */
@HiltViewModel
class BooksViewModel @Inject constructor(var repository: BooksRepository) : ViewModel() {

    private val _books = MutableLiveData<List<Book>>()
    val books: LiveData<List<Book>> = _books

    init {
        _books.value = repository.data
    }

    /**
     * Asynchronously fetches the messages in the Inbox and returns the
     * Result to the associated live data.
     */
    fun getAllBooks() {
        viewModelScope.launch {
            val result = repository.getAll()
            _books.value = result
        }
    }

}