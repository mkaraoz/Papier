package org.mk.papier.ui.words

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mk.papier.data.WordRepository
import org.mk.papier.model.Word

class WordListViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val typeFilter: String? = savedStateHandle["filter"]
    private val allWords: List<Word> = WordRepository(application).loadWords()
        .filter { typeFilter == null || it.type == typeFilter }
        .sortedBy { it.dutch.lowercase() }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    val filteredWords = _searchQuery.map { query ->
        if (query.isBlank()) allWords
        else allWords.filter { word ->
            word.dutch.contains(query, ignoreCase = true) ||
            word.english.contains(query, ignoreCase = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = allWords
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
