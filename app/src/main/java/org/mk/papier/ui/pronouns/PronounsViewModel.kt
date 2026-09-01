package org.mk.papier.ui.pronouns

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import org.mk.papier.data.PronounRepository
import org.mk.papier.model.PronounGroup

class PronounsViewModel(application: Application) : AndroidViewModel(application) {

    private val allGroups: List<PronounGroup> = PronounRepository(application).loadGroups()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery = _searchQuery.asStateFlow()

    /**
     * Search filters the rows inside each group and drops a group once nothing matches,
     * so the headings that stay on screen always describe rows that are actually there.
     */
    val filteredGroups = _searchQuery.map { query ->
        if (query.isBlank()) allGroups
        else allGroups.mapNotNull { group ->
            val matches = group.pronouns.filter { pronoun ->
                pronoun.dutch.contains(query, ignoreCase = true) ||
                pronoun.english.contains(query, ignoreCase = true) ||
                pronoun.unstressed?.contains(query, ignoreCase = true) == true ||
                pronoun.example.contains(query, ignoreCase = true) ||
                pronoun.exampleTranslation.contains(query, ignoreCase = true)
            }
            if (matches.isEmpty()) null else group.copy(pronouns = matches)
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = allGroups
    )

    fun onSearchQueryChange(query: String) {
        _searchQuery.value = query
    }
}
