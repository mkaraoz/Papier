package org.mk.papier.data

import org.mk.papier.model.Category
import org.mk.papier.model.Topic
import org.mk.papier.model.TopicIcon

val allTopics = listOf(
    Topic(
        id = "words",
        title = "Words",
        subtitle = "Lists, themes, idioms & flashcards",
        category = Category.WORDS,
        icon = TopicIcon.BOOK,
        colorHex = 0xFF4A90D9
    ),
    Topic(
        id = "grammar_word_order",
        title = "Word Order",
        subtitle = "Sentence structure",
        category = Category.GRAMMAR,
        icon = TopicIcon.EDIT,
        colorHex = 0xFF4CAF50
    ),
    Topic(
        id = "grammar_tenses",
        title = "Tenses",
        subtitle = "Past, present, future",
        category = Category.GRAMMAR,
        icon = TopicIcon.EDIT,
        colorHex = 0xFF4CAF50
    ),
    Topic(
        id = "grammar_prepositions",
        title = "Prepositions",
        subtitle = "op, om, bij, in...",
        category = Category.GRAMMAR,
        icon = TopicIcon.EDIT,
        colorHex = 0xFF4CAF50
    ),
    Topic(
        id = "basics_alphabet",
        title = "Alphabet",
        subtitle = "Dutch pronunciation",
        category = Category.BASICS,
        icon = TopicIcon.ABC,
        colorHex = 0xFFFF9800
    ),
    Topic(
        id = "basics_numbers",
        title = "Numbers",
        subtitle = "0 to 100 and beyond",
        category = Category.BASICS,
        icon = TopicIcon.NUMBERS,
        colorHex = 0xFFFF9800
    ),
    Topic(
        id = "time_days",
        title = "Days & Months",
        subtitle = "Calendar",
        category = Category.TIME,
        icon = TopicIcon.CALENDAR,
        colorHex = 0xFFE91E63
    ),
    Topic(
        id = "time_telling",
        title = "Telling Time",
        subtitle = "Reading the clock",
        category = Category.TIME,
        icon = TopicIcon.CLOCK,
        colorHex = 0xFFE91E63
    ),
)