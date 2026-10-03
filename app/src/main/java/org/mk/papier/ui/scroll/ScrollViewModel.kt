package org.mk.papier.ui.scroll

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import org.mk.papier.data.ScrollRepository
import org.mk.papier.model.ScrollWord

class ScrollViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = ScrollRepository(application)
    val words: List<ScrollWord> = repository.words
}
