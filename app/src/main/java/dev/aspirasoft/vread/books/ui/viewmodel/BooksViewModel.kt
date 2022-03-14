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

    private val _books = MutableLiveData<Result<List<Book>>>()
    val books: LiveData<Result<List<Book>>> = _books

    init {
        repository.books?.let {
            _books.value = Result.success(it)
        }
    }

    /**
     * Asynchronously fetches the messages in the Inbox and returns the
     * Result to the associated live data.
     */
    fun getAllBooks() {
        viewModelScope.launch {
            val result = repository.listAll()
            _books.value = result
        }
    }

}