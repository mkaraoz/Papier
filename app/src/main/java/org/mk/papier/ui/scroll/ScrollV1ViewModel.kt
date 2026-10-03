package org.mk.papier.ui.scroll

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.mk.papier.data.ScrollRepository

/** Retains the local ratings from the original card experiment. */
class ScrollV1ViewModel(application: Application) : AndroidViewModel(application) {
    val words = ScrollRepository(application).words
    private val preferences = application.getSharedPreferences("dutch_scroll", Context.MODE_PRIVATE)
    private val _ratings = MutableStateFlow(words.mapNotNull { item ->
        preferences.getString(item.word.id, null)?.let { item.word.id to it }
    }.toMap())
    val ratings = _ratings.asStateFlow()

    fun rate(wordId: String, rating: String) {
        preferences.edit().putString(wordId, rating).apply()
        _ratings.value = _ratings.value + (wordId to rating)
    }
}
