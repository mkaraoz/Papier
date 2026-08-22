package org.mk.papier.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.mk.papier.data.allTopics
import org.mk.papier.model.Category
import org.mk.papier.model.Topic

class HomeViewModel : ViewModel() {

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _selectedCategory = MutableStateFlow(Category.ALL)
    val selectedCategory = _selectedCategory.asStateFlow()

    val filteredTopics = combine(_searchQuery, _selectedCategory) { query, category ->
        allTopics.filter { topic ->
            (category == Category.ALL || topic.category == category) &&
            (query.isBlank() || topic.title.contains(query, ignoreCase = true) ||
                    topic.subtitle.contains(query, ignoreCase = true))
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = allTopics
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun onCategorySelected(category: Category) {
        _selectedCategory.value = category
    }
}