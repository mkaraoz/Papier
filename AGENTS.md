# Papier project context

## Purpose and content
- Personal Android app for learning Dutch at A2 level; not intended for Play Store publication.
- Keep vocabulary and example sentences at A2 or below.
- Mixed translation languages are intentional: vocabulary and practice sentences use English; idioms use Turkish translations and explanations plus an English equivalent. Do not standardize them to one language.

## Collaboration preferences
- Discuss the implementation plan and get the user's agreement before coding.
- Before committing, explain what is ready and ask the user to test it. Wait for their confirmation.
- Before pushing, show the exact commits and destination and obtain a separate confirmation immediately before the push. Follow the user's current explicit instructions if they change this workflow.
- Preserve existing user edits and avoid unrelated changes.
- The user wants to run device/UI tests themselves. Do not start emulator/device tests unless requested.

## UI decisions
- Layout stability matters: reserve space for optional badges and controls; avoid unexpected movement when state changes. Intentional expansion to reveal examples or translations is part of the design.
- Keep de/het badges inline before the Dutch word. Do not show word-type labels in the UI.
- Word-type filtering is via navigation/ViewModel, not extra filter chips. Existing Sorted/Random controls are intentional.
- Speech controls belong only on the word list unless the user requests otherwise. Keep unavailable speech controls visible but disabled.
- Pronouns: do not restore the removed reflexive-pronoun section or Turkish note boxes. Question/indefinite pronouns and `er` are outside the current scope.
- Every screen root must account for both system bars: background before `systemBarsPadding()`. Three-button navigation previously obscured bottom content.

## Architecture and implementation
- Kotlin, Jetpack Compose, MVVM with StateFlow, Navigation Compose, Gson; single app module.
- MainActivity defines routes; optional filter/theme arguments reach ViewModels through SavedStateHandle.
- AGP 9.x provides built-in Kotlin: do not add the kotlin-android plugin or legacy kotlinOptions configuration.
- Verify suspected IDE-only Compose errors with `./gradlew assembleDebug`.
- Content is bundled under app/src/main/assets. WordRepository loads vocabulary.json; test_words.json is the small development fixture.
- Themes reference the master vocabulary. Homonyms can use a sense-qualified key such as `dag (day)`; speech reads only the Dutch word.
- sentences.json is a bare array. dutch_phrases.json has a metadata wrapper; update its version/date when editing phrases.
- Phrase ordering and pronoun group ordering follow their files. If phrase flashcards are added, use examples[0] on the face to avoid varying example counts disrupting the layout.

## State recorded on 2026-09-27
- Implemented: home, Words hub, searchable word list, verb filtering, New Words, themes, idioms, practice sentences, pronouns, word-list Dutch TTS.
- New Words replaces both removed flashcard modes. It reuses WordListScreen and stores selected word IDs in private SharedPreferences via NewWordsRepository. Left-to-right swipes in All Words/Verbs/Themes add without confirmation or duplicates; in New Words they ask before removing only the selection. Search, sorting, examples and speech remain available. Build and repository instrumentation checks passed; the user will test the UI.
- Content checked: 526 words, 40 phrases, 91 sentences, 39 pronoun forms in 5 groups, 8 themes. Counts will change; inspect assets for current values.
- Imported 97 new vocabulary entries from app/pdf/51.pdf through 54.pdf (housing/home/study vocabulary), IDs 430–526. Skipped existing mooi, vinden and zomer; distinguished adjective/noun licht with sense labels. Added English translations and simple original examples. JSON structure, unique IDs, full PDF coverage and preservation of existing entries were checked; device testing is pending.
- Home routes only Words and Pronouns. Other home topics are placeholders.
- Planned, not implemented: grammar (word order/tenses/prepositions), basics (alphabet/numbers), time screens, manually triggered public Google Drive JSON sync with local cache. Do not restore flashcards without a new request.
- Whether themes should cover every word or remain curated is undecided.
- README is outdated. Initial review checked JSON parsing, duplicate word IDs and theme references, but did not run a build/device test.

## Historical context
- Imported at the user's request from `/home/mk/.claude/projects/-home-mk-AndroidStudioProjects-Papier/memory/`.
- That directory has MEMORY.md, project_overview.md, architecture.md, screens_built.md, user_preferences.md and screen-insets.md.
- Historical counts and some descriptions are stale; prefer current code/assets for implementation facts and the user's latest instructions for preferences.
- Keep this file current when agreed decisions or project status materially change. Never store credentials here.
