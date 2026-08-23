package org.mk.papier.model

import com.google.gson.annotations.SerializedName

/**
 * A set phrase from dutch_phrases.json. Unlike [Word] the translation side is
 * Turkish: `translation` and `explanation` carry the L1 depth, `en` is the
 * English equivalent kept as a second angle on the Dutch structure.
 */
data class Phrase(
    val id: Int,
    val phrase: String,
    val translation: String,
    val en: String,
    val explanation: String,
    val examples: List<PhraseExample>,
    val tags: List<String>
)

data class PhraseExample(
    val nl: String,
    val tr: String
)

data class PhraseFile(
    val meta: PhraseMeta,
    val phrases: List<Phrase>
)

data class PhraseMeta(
    @SerializedName("language_from") val languageFrom: String,
    @SerializedName("language_to") val languageTo: String,
    val version: Int,
    @SerializedName("updated_at") val updatedAt: String
)
