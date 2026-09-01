package org.mk.papier.model

/**
 * One row of a pronoun table. [unstressed] is the reduced form heard in everyday speech
 * (jij → je, mij → me) and is null when the pronoun has none.
 */
data class Pronoun(
    val id: String,
    val dutch: String,
    val unstressed: String?,
    val english: String,
    val example: String,
    val exampleTranslation: String
)

data class PronounGroup(
    val id: String,
    val title: String,
    val pronouns: List<Pronoun>
)

data class PronounFile(
    val groups: List<PronounGroup>
)
