package org.mk.papier.model

/**
 * An A2 practice sentence from sentences.json. Dutch first, English behind a tap —
 * the point is reading the Dutch, so the translation is not shown until asked for.
 */
data class Sentence(
    val id: Int,
    val sentence: String,
    val english: String
)
