package org.mk.papier.data

import android.content.Context
import android.content.SharedPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

/** Personal selection only; definitions always come from the bundled vocabulary. */
class NewWordsRepository internal constructor(
    private val preferences: SharedPreferences
) : AutoCloseable {
    constructor(context: Context) : this(
        context.applicationContext.getSharedPreferences("new_words", Context.MODE_PRIVATE)
    )

    private fun readIds(): Set<String> = preferences.getStringSet("word_ids", emptySet())
        .orEmpty().toSet()

    private val _wordIds = MutableStateFlow(readIds())
    val wordIds = _wordIds.asStateFlow()

    // Keep open list screens in sync, including when returning from another screen.
    private val listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
        if (key == "word_ids" || key == null) _wordIds.value = readIds()
    }

    init {
        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    fun add(wordId: String): Boolean {
        val current = readIds()
        if (wordId in current) return false
        save(current + wordId)
        return true
    }

    fun remove(wordId: String) {
        save(readIds() - wordId)
    }

    private fun save(ids: Set<String>) {
        // Never mutate the Set returned by SharedPreferences.
        preferences.edit().putStringSet("word_ids", ids).apply()
        _wordIds.value = ids
    }

    override fun close() {
        preferences.unregisterOnSharedPreferenceChangeListener(listener)
    }
}
