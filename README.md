# WordForge

A small Android app for learning vocabulary with spaced repetition. Add words you want to remember; they come back for review at growing intervals — short at first, longer as they stick.

## Features

- 9-tier spaced-repetition curve (1 hour → 30 days), with ±10% jitter so reviews don't pile up at the same minute
- Hidden meaning by default — tap to reveal during review or in the detail screen
- Local notifications when a word is due
- Built-in tier reference (help icon → "How it works")
- Material 3 design, light/dark, edge-to-edge

## Build

Open the project in Android Studio (it bundles a compatible JDK and Android SDK) and let it sync.

From the command line:

```bash
./gradlew :app:assembleDebug   # APK lands in app/build/outputs/apk/debug/
./gradlew :app:installDebug    # install on a connected device or running emulator
```

Requires JDK 21 and Android SDK 36 (both bundled with Android Studio).
