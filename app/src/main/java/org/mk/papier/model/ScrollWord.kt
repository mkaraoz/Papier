package org.mk.papier.model

/** Additional content for the first Dutch Scroll experiment. Word facts come from vocabulary.json. */
data class ScrollWordContent(
    val wordId: String,
    val firstHighlight: String,
    val examples: List<ScrollExample>,
    val cefr: String? = null
)

data class ScrollExample(
    val dutch: String,
    val english: String,
    val highlight: String
)

data class ScrollContentList(val words: List<ScrollWordContent>)

data class ScrollWord(
    val word: Word,
    val examples: List<ScrollExample>,
    val cefr: String?
)
