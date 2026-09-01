package org.mk.papier.data

import android.content.Context
import android.util.Log
import com.google.gson.Gson
import org.mk.papier.model.Theme

class ThemeRepository(private val context: Context) {

    /**
     * Themes keep their order from themes.json — that's the order of the chips on screen.
     * Words listed in a theme but missing from the master list are skipped and logged,
     * so a typo shows up in Logcat instead of a word quietly disappearing.
     */
    fun loadThemes(): List<Theme> {
        val json = context.assets.open(THEMES_FILE).bufferedReader().use { it.readText() }
        val themes = Gson().fromJson(json, ThemeFile::class.java).themes

        val allWords = WordRepository(context).loadWords()

        // A word is normally reachable by its plain Dutch spelling. Homonyms carry a sense
        // indicator, and each sense is reachable as "dag (day)". The bare spelling is then
        // ambiguous, so it is left out of the map on purpose — a theme listing plain "dag"
        // gets the missing-word warning below instead of an arbitrary one of the two.
        val bySense = allWords.associateBy { it.themeKey.lowercase() }
        val ambiguous = allWords.groupBy { it.dutch.lowercase() }
            .filterValues { it.size > 1 }
            .keys
        val wordsByDutch = bySense + allWords
            .filter { it.dutch.lowercase() !in ambiguous }
            .associateBy { it.dutch.lowercase() }

        return themes.mapNotNull { (name, dutchWords) ->
            val missing = dutchWords.filterNot { wordsByDutch.containsKey(it.lowercase()) }
            if (missing.isNotEmpty()) {
                Log.w(TAG, "Theme \"$name\" — not in vocabulary.json: ${missing.joinToString()}")
            }
            val words = dutchWords.mapNotNull { wordsByDutch[it.lowercase()] }
            if (words.isEmpty()) null else Theme(name = name, words = words)
        }
    }

    private data class ThemeFile(
        val themes: LinkedHashMap<String, List<String>>
    )

    companion object {
        private const val THEMES_FILE = "themes.json"
        private const val TAG = "ThemeRepository"
    }
}
