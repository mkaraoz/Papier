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

To build a signed release APK ready to install:

```bash
./build-apk.sh
```

The script creates `build/apk/Papier_yyyyMMdd_HHmmss.apk` (for example, `Papier_20260927_143052.apk`), aligns it, signs it and verifies the signature. The timestamp uses the computer's local time and previous APKs are retained. It does not install the app or run device tests. Run it from any directory. It requires Linux, a Java JDK, OpenSSL, util-linux and Android SDK Build Tools 35+ (36 is installed in the current environment). The SDK is located through `ANDROID_HOME`, `ANDROID_SDK_ROOT`, `local.properties`, or `~/Android/Sdk`.

On the first run it creates a private release key and random password in `.signing/`, which Git ignores. Later builds reuse the same key. Back up the entire `.signing/` directory securely: updates must use the same signing key. The password is read from a file rather than passed on the command line. Alignment happens before signing, as required by [Android's apksigner documentation](https://developer.android.com/tools/apksigner).

### Release signing files

All paths are relative to the project root:

| Item | Location / value |
| --- | --- |
| Signing key (PKCS12) | `.signing/papier-release.p12` |
| Keystore and key password | `.signing/password` |
| Key alias | `papier` |
| Build command | `./build-apk.sh` |

The `.signing` directory is hidden; enable “Show hidden files” in your file manager to see it. Back up both the key and password together outside this checkout. On another computer, restore them to the same paths before running the script. A Git clone alone does not contain these files. If either file is missing, recover the original from backup rather than creating a replacement key. Never copy the password or key into documentation or commit them to Git.

A release APK signed with this new key cannot update an existing debug-signed installation in place. Keep that installation and its New Words data until you have arranged a migration; uninstalling it can remove the selection. Subsequent APKs produced by this script share the release key and can update each other.

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
