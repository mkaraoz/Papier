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
import org.mk.papier.data.ThemeRepository
import org.mk.papier.data.NewWordsRepository
import org.mk.papier.data.WordRepository
import org.mk.papier.model.Word

enum class SortMode { SORTED, RANDOM }

class WordListViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val typeFilter: String? = savedStateHandle["filter"]
    private val themeFilter: String? = savedStateHandle["theme"]
    val isNewWords: Boolean = savedStateHandle["newWords"] ?: false
    private val newWordsRepository = NewWordsRepository(application)

    /** Header title — the theme name when we arrived from the Themes screen. */
    val title: String = if (isNewWords) "New Words"
        else themeFilter?.replaceFirstChar { it.uppercase() } ?: "Word List"

    private val allWords: List<Word> = if (themeFilter != null) {
        ThemeRepository(application).loadThemes()
            .firstOrNull { it.name.equals(themeFilter, ignoreCase = true) }
            ?.words
            .orEmpty()
    } else {
        WordRepository(application).loadWords()
    }

    private val sortedWords: List<Word> = allWords
        .filter { typeFilter == null || it.type == typeFilter }
        .sortedBy { it.dutch.lowercase() }

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    private val _sortMode = MutableStateFlow(SortMode.SORTED)
    val sortMode = _sortMode.asStateFlow()

    private val _currentWords = MutableStateFlow(sortedWords)

    val filteredWords = combine(
        _searchQuery, _currentWords, newWordsRepository.wordIds
    ) { query, words, selectedIds ->
        words.filter { word ->
            (!isNewWords || word.id in selectedIds) && (
                query.isBlank() ||
                word.dutch.contains(query, ignoreCase = true) ||
                word.english.contains(query, ignoreCase = true) ||
                word.sense?.contains(query, ignoreCase = true) == true
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = sortedWords.filter { !isNewWords || it.id in newWordsRepository.wordIds.value }
    )

    fun wordById(id: String): Word? = allWords.firstOrNull { it.id == id }

    fun addToNewWords(word: Word): Boolean = newWordsRepository.add(word.id)

    fun removeFromNewWords(wordId: String) {
        newWordsRepository.remove(wordId)
    }

    override fun onCleared() {
        newWordsRepository.close()
        super.onCleared()
    }

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
