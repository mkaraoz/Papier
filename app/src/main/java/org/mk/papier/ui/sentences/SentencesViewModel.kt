package org.mk.papier.ui.sentences

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mk.papier.data.SentenceRepository
import org.mk.papier.model.Sentence

class SentencesViewModel(application: Application) : AndroidViewModel(application) {

    private val allSentences: List<Sentence> = SentenceRepository(application).loadSentences()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    /** Search covers both sides, so "market" finds "markt" sentences too. */
    val filteredSentences = _searchQuery.map { query ->
        if (query.isBlank()) allSentences
        else allSentences.filter { sentence ->
            sentence.sentence.contains(query, ignoreCase = true) ||
            sentence.english.contains(query, ignoreCase = true)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = allSentences
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
