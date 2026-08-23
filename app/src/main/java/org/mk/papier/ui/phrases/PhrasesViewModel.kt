package org.mk.papier.ui.phrases

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mk.papier.data.PhraseRepository
import org.mk.papier.model.Phrase

class PhrasesViewModel(application: Application) : AndroidViewModel(application) {

    private val allPhrases: List<Phrase> = PhraseRepository(application).loadPhrases()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    /** Search covers both translation sides and the tags, so "shopping" and "alışveriş" both hit. */
    val filteredPhrases = _searchQuery.map { query ->
        if (query.isBlank()) allPhrases
        else allPhrases.filter { phrase ->
            phrase.phrase.contains(query, ignoreCase = true) ||
            phrase.translation.contains(query, ignoreCase = true) ||
            phrase.en.contains(query, ignoreCase = true) ||
            phrase.tags.any { it.contains(query, ignoreCase = true) }
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = allPhrases
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
