package org.mk.papier.ui.words

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import org.mk.papier.data.WordRepository
import org.mk.papier.model.Word

enum class SortMode { SORTED, RANDOM }

class WordListViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val typeFilter: String? = savedStateHandle["filter"]
    private val sortedWords: List<Word> = WordRepository(application).loadWords()
        .filter { typeFilter == null || it.type == typeFilter }
        .sortedBy { it.dutch.lowercase() }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortMode = MutableStateFlow(SortMode.SORTED)
    val sortMode = _sortMode.asStateFlow()

    private val _currentWords = MutableStateFlow(sortedWords)

    val filteredWords = combine(_searchQuery, _currentWords) { query, words ->
        if (query.isBlank()) words
        else words.filter { word ->
            word.dutch.contains(query, ignoreCase = true) ||
            word.english.contains(query, ignoreCase = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = sortedWords
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }

    fun setSortMode(mode: SortMode) {
        _sortMode.value = mode
        _currentWords.value = if (mode == SortMode.SORTED) {
            sortedWords
        } else {
            sortedWords.shuffled(kotlin.random.Random(System.currentTimeMillis()))
        }
    }
}
