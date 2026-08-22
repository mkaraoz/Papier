package org.mk.papier.model

data class Word(
    val id: String,
    val dutch: String,
    val article: String?,
    val english: String,
    val type: String,
    val example: String,
    val exampleTranslation: String
)

data class WordList(
    val words: List<Word>
)
