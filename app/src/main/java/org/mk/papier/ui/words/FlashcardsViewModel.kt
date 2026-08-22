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

class FlashcardsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val typeFilter: String? = savedStateHandle["filter"]
    private val words: List<Word> = WordRepository(application).loadWords()
        .filter { typeFilter == null || it.type == typeFilter }
        .shuffled()

    val totalCount: Int = words.size

    private val _currentIndex = MutableStateFlow(0)
    val currentIndex = _currentIndex.asStateFlow()

    val currentWord = _currentIndex.map { words.getOrNull(it) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = words.firstOrNull()
        )

    fun next() {
        if (_currentIndex.value < words.size - 1) _currentIndex.value++
    }

    fun previous() {
        if (_currentIndex.value > 0) _currentIndex.value--
    }
}
