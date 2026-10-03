package org.mk.papier.ui.scroll

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.random.Random
import org.mk.papier.data.NewWordsRepository
import org.mk.papier.data.ScrollRepository
import org.mk.papier.model.ScrollWord

class ScrollV4ViewModel(application: Application) : AndroidViewModel(application) {
    val words: List<ScrollWord> = ScrollRepository(application).words
    private val newWordsRepository = NewWordsRepository(application)
    val savedIds: StateFlow<Set<String>> = newWordsRepository.wordIds

    private val preferences = application.getSharedPreferences("dutch_scroll_known", Context.MODE_PRIVATE)
    private val _knownIds = MutableStateFlow(preferences.getStringSet("word_ids", emptySet()).orEmpty().toSet())
    val knownIds = _knownIds.asStateFlow()

    // Once a page is shown, its word stays fixed even if Know changes the next pass.
    private val assignedPages = words.toMutableList()
    private var passNumber = 1

    fun wordAt(page: Int): ScrollWord {
        while (assignedPages.size <= page) {
            val candidates = words.filter { it.word.id !in _knownIds.value }.ifEmpty { words }
            val nextPass = candidates.shuffled(Random(passNumber++)).toMutableList()
            if (nextPass.size > 1 && nextPass.first().word.id == assignedPages.last().word.id) {
                val first = nextPass.removeAt(0)
                nextPass.add(1, first)
            }
            assignedPages.addAll(nextPass)
        }
        return assignedPages[page]
    }

    fun save(wordId: String) {
        newWordsRepository.add(wordId)
    }

    fun toggleKnown(wordId: String) {
        val updated = if (wordId in _knownIds.value) _knownIds.value - wordId
            else _knownIds.value + wordId
        preferences.edit().putStringSet("word_ids", updated).apply()
        _knownIds.value = updated
    }

    override fun onCleared() {
        newWordsRepository.close()
        super.onCleared()
    }
}
