package org.mk.papier.data

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import org.mk.papier.model.Sentence

class SentenceRepository(private val context: Context) {

    /**
     * sentences.json is a bare array — no `meta` wrapper like dutch_phrases.json —
     * so it needs a TypeToken rather than a file class. Order is the file's order,
     * which is the curated teaching order.
     */
    fun loadSentences(): List<Sentence> {
        val json = context.assets.open(SENTENCES_FILE).bufferedReader().use { it.readText() }
        val type = object : TypeToken<List<Sentence>>() {}.type
        return Gson().fromJson(json, type)
    }

    companion object {
        private const val SENTENCES_FILE = "sentences.json"
    }
}
