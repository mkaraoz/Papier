package org.mk.papier.data

import android.content.Context
import com.google.gson.Gson
import org.mk.papier.model.Phrase
import org.mk.papier.model.PhraseFile

class PhraseRepository(private val context: Context) {

    /** Phrases keep their order from the file — that's the curated teaching order. */
    fun loadPhrases(): List<Phrase> {
        val json = context.assets.open(PHRASES_FILE).bufferedReader().use { it.readText() }
        return Gson().fromJson(json, PhraseFile::class.java).phrases
    }

    companion object {
        private const val PHRASES_FILE = "dutch_phrases.json"
    }
}
