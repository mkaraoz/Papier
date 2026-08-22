# Papier

A personal Dutch language learning app for an A2-level learner. *Papier* means "paper" in Dutch — the icon is a paper plane on a deep blue background. Built for personal use (not published to the Play Store).

## Features

- **Words** — flat list of learned Dutch words with search, A–Z / random sorting, and expandable rows showing an example sentence and its translation.
- **Flashcards** — swipeable cards (Dutch on top, tap to reveal the English translation and example).
- **Verb filtering** — dedicated views for verbs, both as a list and as flashcards.

Planned topics: Grammar (word order, tenses, prepositions), Basics (alphabet, numbers), Time (days/months, telling time), and Idioms.

## Tech stack

- **Kotlin** + **Jetpack Compose** (no XML layouts)
- **MVVM** with `StateFlow`
- **Jetpack Navigation Compose** for screen routing
- **Gson** for JSON parsing
- Android Gradle Plugin 9.3.1, Kotlin 2.0.21, Compose BOM 2024.12.01

## Data

Word content lives in `app/src/main/assets/words.json` (id, dutch, article, english, type, example, exampleTranslation). All content is kept at A2 level or below. A future update will sync the latest word list from a public Google Drive link, falling back to the bundled JSON.

## Building

```bash
./gradlew assembleDebug
```

The debug APK is produced under `app/build/outputs/apk/debug/`.

## Project structure

```
org.mk.papier/
  MainActivity.kt        — NavHost with all routes
  model/                 — Topic, Word data classes
  data/                  — TopicRepository, WordRepository (reads words.json)
  ui/
    home/                — HomeScreen + HomeViewModel
    words/               — WordsHubScreen, WordListScreen, FlashcardsScreen + ViewModels
    theme/               — PapierTheme
```
