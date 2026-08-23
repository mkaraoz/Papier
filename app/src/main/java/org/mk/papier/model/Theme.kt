package org.mk.papier.model

/**
 * A theme is just an index into the master word list — themes.json holds only
 * the Dutch words, everything else is looked up in vocabulary.json.
 */
data class Theme(
    val name: String,
    val words: List<Word>
)
