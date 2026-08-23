package org.mk.papier.model

enum class Category(val displayName: String) {
    ALL("All"),
    WORDS("Words"),
    GRAMMAR("Grammar"),
    BASICS("Basics"),
    TIME("Time"),
    IDIOMS("Idioms")
}

enum class TopicIcon {
    BOOK, EDIT, ABC, NUMBERS, CALENDAR, CLOCK, CHAT, LABEL
}

data class Topic(
    val id: String,
    val title: String,
    val subtitle: String,
    val category: Category,
    val icon: TopicIcon,
    val colorHex: Long
)