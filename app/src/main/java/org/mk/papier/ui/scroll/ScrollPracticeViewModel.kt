package org.mk.papier.ui.scroll

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import org.mk.papier.data.ScrollRepository
import org.mk.papier.model.ScrollWord

class ScrollPracticeViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {
    val item: ScrollWord? = ScrollRepository(application).words
        .firstOrNull { it.word.id == savedStateHandle.get<String>("wordId") }
}
