package org.mk.papier.ui.scroll

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.StateFlow
import org.mk.papier.data.NewWordsRepository
import org.mk.papier.data.ScrollRepository

class ScrollV3ViewModel(application: Application) : AndroidViewModel(application) {
    val words = ScrollRepository(application).words
    private val newWordsRepository = NewWordsRepository(application)
    val savedWordIds: StateFlow<Set<String>> = newWordsRepository.wordIds

    fun saveWord(wordId: String) {
        newWordsRepository.add(wordId)
    }

    override fun onCleared() {
        newWordsRepository.close()
        super.onCleared()
    }
}
