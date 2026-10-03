package org.mk.papier.data

import android.content.Context
import com.google.gson.Gson
import org.mk.papier.model.ScrollContentList
import org.mk.papier.model.ScrollExample
import org.mk.papier.model.ScrollWord

class ScrollRepository(context: Context) {
    private val appContext = context.applicationContext

    val words: List<ScrollWord> = run {
        val vocabulary = WordRepository(appContext).loadWords().associateBy { it.id }
        val json = appContext.assets.open("scroll_content.json").bufferedReader().use { it.readText() }
        Gson().fromJson(json, ScrollContentList::class.java).words.map { content ->
            val word = requireNotNull(vocabulary[content.wordId]) {
                "Unknown Dutch Scroll word ID: ${content.wordId}"
            }
            require(content.examples.size == 2) { "Dutch Scroll needs two added examples per word" }
            ScrollWord(
                word = word,
                examples = listOf(
                    ScrollExample(word.example, word.exampleTranslation, content.firstHighlight)
                ) + content.examples,
                cefr = content.cefr
            )
        }
    }
}
