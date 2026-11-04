# Taskee - Minimalist Task & Notes Manager for Android

Taskee is a native Android application built with **Kotlin** and **Jetpack Compose**, rewritten from the original Flutter repository while faithfully preserving all features, UX flows, animations, and dark design aesthetics.

## Features
- **Task Management**: Create, update, mark as complete/pending, and delete tasks with due date and time scheduling.
- **Notes Manager**: Create, edit, and navigate notes with an interactive card stack and swipe controls.
- **Offline-First Persistence**: Local storage with instant responsiveness and state recovery.
- **Undo Actions**: Instant undo support via Snackbar when tasks are removed.
- **Material 3 Dark Theme**: Polished dark theme with accent lime highlights (`#D4E157` / `#D8E87C`).

## Mobile APK
The ready-to-install Android APK is generated at the root of the project:
- `taskee.apk` (and `Taskee.apk`)
- Also located at `app/build/outputs/apk/debug/app-debug.apk`

## Tech Stack
- **Language**: Kotlin 2.2.10
- **UI Toolkit**: Jetpack Compose (Material 3)
- **Build System**: Gradle 9.3.1 with Android Gradle Plugin 9.1.1
- **Architecture**: MVVM with Kotlin Coroutines and StateFlow
- **Target SDK**: Android 36 (Android 16 / Upside Down Cake+)
- **Min SDK**: Android 26 (Android 8.0 Oreo)
