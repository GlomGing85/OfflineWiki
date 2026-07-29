package com.offlinewiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinewiki.data.Article
import com.offlinewiki.data.WikiRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel(private val repository: WikiRepository) : ViewModel() {
    private val _query = MutableStateFlow("")
    val searchResults: StateFlow<List<Article>> = _query
        .debounce(300)
        .map { query ->
            if (query.isBlank()) emptyList()
            else repository.search(query).first()
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun search(query: String) {
        _query.value = query
    }
}
