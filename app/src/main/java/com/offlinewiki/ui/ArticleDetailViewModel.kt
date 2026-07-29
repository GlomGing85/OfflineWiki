package com.offlinewiki.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.offlinewiki.data.Article
import com.offlinewiki.data.WikiRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArticleDetailViewModel(private val repository: WikiRepository, private val articleId: String) : ViewModel() {
    private val _article = kotlinx.coroutines.flow.MutableStateFlow<Article?>(null)
    val article: StateFlow<Article?> = _article

    init {
        viewModelScope.launch {
            _article.value = repository.getArticle(articleId)
        }
    }

    fun deleteArticle(article: Article) {
        viewModelScope.launch { repository.deleteArticle(article) }
    }
}
