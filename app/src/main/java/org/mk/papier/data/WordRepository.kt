package org.mk.papier.data

import android.content.Context
import com.google.gson.Gson
import org.mk.papier.model.Word
import org.mk.papier.model.WordList

class WordRepository(private val context: Context) {

    fun loadWords(): List<Word> {
        val json = context.assets.open(WORDS_FILE).bufferedReader().use { it.readText() }
        return Gson().fromJson(json, WordList::class.java).words
    }

    companion object {
        // Release word list — the full vocabulary. This keeps growing.
        // For quick feature testing, swap to the small "test_words.json" test set.
        private const val WORDS_FILE = "vocabulary.json"
    }
}
