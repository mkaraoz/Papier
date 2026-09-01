package org.mk.papier.model

data class Word(
    val id: String,
    val dutch: String,
    /**
     * Sense indicator for homonyms — the short parenthetical a dictionary puts next to a
     * headword when the same spelling covers unrelated meanings ("dag (greeting)" vs
     * "dag (day)"). Null for every ordinary word, so only the handful that clash carry it.
     * Never spoken: DutchTts reads [dutch] alone.
     */
    val sense: String? = null,
    val article: String?,
    val english: String,
    val type: String,
    val example: String,
    val exampleTranslation: String
) {
    /** How themes.json refers to this word — qualified only when a sense is set. */
    val themeKey: String get() = if (sense == null) dutch else "$dutch ($sense)"
}

data class WordList(
    val words: List<Word>
)
