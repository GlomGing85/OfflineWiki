package com.offlinewiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinewiki.data.Article
import com.offlinewiki.data.Folder
import com.offlinewiki.data.WikiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn

class MainViewModel(private val repository: WikiRepository) : ViewModel() {
    val articles = repository.getAllArticles().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val folders = repository.getAllFolders().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
}
