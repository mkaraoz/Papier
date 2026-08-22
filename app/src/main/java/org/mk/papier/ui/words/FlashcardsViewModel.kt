package org.mk.papier.ui.words

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import org.mk.papier.data.WordRepository
import org.mk.papier.model.Word

class FlashcardsViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val typeFilter: String? = savedStateHandle["filter"]

    val words: List<Word> = WordRepository(application).loadWords()
        .filter { typeFilter == null || it.type == typeFilter }
        .shuffled(kotlin.random.Random(System.currentTimeMillis()))

    val totalCount: Int = words.size
}
