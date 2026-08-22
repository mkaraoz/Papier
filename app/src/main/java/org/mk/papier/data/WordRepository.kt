package org.mk.papier.data

import android.content.Context
import com.google.gson.Gson
import org.mk.papier.model.Word
import org.mk.papier.model.WordList

class WordRepository(private val context: Context) {

    fun loadWords(): List<Word> {
        val json = context.assets.open("words.json").bufferedReader().use { it.readText() }
        return Gson().fromJson(json, WordList::class.java).words
    }
}
